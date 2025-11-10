import ShortenUrlForm from "./ShortenUrlForm";
import { render, screen } from "@testing-library/react";
import { http, HttpResponse } from "msw";
import "@testing-library/jest-dom";
import userEvent from "@testing-library/user-event";
import { setupServer } from "msw/node";

// https://testing-library.com/docs/react-testing-library/example-intro
const server = setupServer();
beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

function prepareGeneratedUidServerMock(mockUid: string = "abcabcabc1") {
  server.use(
    http.post("http://localhost:8080/unique-identifiers", () => {
      return HttpResponse.json({
        unique_identifier: mockUid,
      });
    }),
  );
}

function prepareShortenedUrlInfoServerMock(uid: string = "abcabcabc1") {
  server.use(
    http.get(`http://localhost:8080/shortened-urls/${uid}`, () => {
      return HttpResponse.json({}, { status: 404 });
    }),
  );
}

it("renders default children when accessed", () => {
  prepareGeneratedUidServerMock();
  render(<ShortenUrlForm />);
  expect(screen.getByTestId("tabs-selection")).toBeDefined();
  expect(screen.getByTestId("url-input")).toBeDefined();
  expect(screen.getByTestId("shorten-button-input")).toBeDefined();
  expect(screen.getByTestId("shorten-button-input")).toBeDisabled();
});

it("button is disabled while the input is not correct", async () => {
  prepareGeneratedUidServerMock();
  prepareShortenedUrlInfoServerMock();
  const user = userEvent.setup();
  render(<ShortenUrlForm />);
  const shortenButton = screen.getByTestId("shorten-button-input");

  const urlInput = screen.getByTestId("url-input");
  await user.type(urlInput, "www");

  expect(shortenButton).toBeDisabled();

  await user.type(urlInput, "http://");
  expect(shortenButton).toBeDisabled();

  await user.clear(urlInput);
  await user.type(urlInput, "www.google");
  expect(shortenButton).toBeEnabled();
});

it("button is disabled when the uid input is not correct", async () => {
  prepareGeneratedUidServerMock();
  prepareShortenedUrlInfoServerMock();
  const user = userEvent.setup();
  render(<ShortenUrlForm />);
  const selection = screen.getByTestId("tabs-selection-1");
  await user.click(selection);

  const shortenButton = screen.getByTestId("shorten-button-input");
  expect(shortenButton).toBeDisabled();

  const uidInput = screen.getByTestId("unique-id-input");
  await user.type(uidInput, "abcabcabc11");
  expect(shortenButton).toBeDisabled();

  await user.clear(uidInput);
  await user.type(uidInput, "##");
  expect(shortenButton).toBeDisabled();

  prepareShortenedUrlInfoServerMock("o");
  await user.clear(uidInput);
  await user.type(uidInput, "o");
  const urlInput = screen.getByTestId("url-input");
  await user.type(urlInput, "www.example.com");

  expect(shortenButton).toBeEnabled();
});

it("should regenerate unique id when refresh button is pressed", async () => {
  prepareGeneratedUidServerMock();
  const user = userEvent.setup();
  render(<ShortenUrlForm />);
  const customTab = screen.getByTestId("tabs-selection-1");
  await user.click(customTab);

  const uidInput = screen.getByTestId("unique-id-input");
  const newId = "test2";
  prepareShortenedUrlInfoServerMock(newId);
  prepareGeneratedUidServerMock(newId);
  const refreshButton = screen.getByTestId("refresh-button");
  await user.click(refreshButton);
  expect(uidInput).toHaveValue(newId);
});

it("show success dialog with shortened url when button is pressed", async () => {
  prepareGeneratedUidServerMock();
  const user = userEvent.setup();
  render(<ShortenUrlForm />);
  server.use(
    http.post("http://localhost:8080/shorten", () => {
      return HttpResponse.json({
        shortened_url: "http://www.example.com/goto/jsjsjsj",
      });
    }),
  );
  const shortenButton = screen.getByTestId("shorten-button-input");

  const urlInput = screen.getByTestId("url-input");
  await user.type(urlInput, "www.example.com");

  await user.click(shortenButton);
  expect(await screen.findByTestId("success-dialog")).toBeDefined();
});
