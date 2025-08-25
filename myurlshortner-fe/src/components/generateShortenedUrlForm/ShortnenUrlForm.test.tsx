import ShortenUrlForm from "./ShortenUrlForm";
import { render, screen } from "@testing-library/react";
import {http, HttpResponse} from 'msw'
import '@testing-library/jest-dom';
import userEvent from '@testing-library/user-event'
import {setupServer} from 'msw/node'

// https://testing-library.com/docs/react-testing-library/example-intro
const server = setupServer()
beforeAll(() => server.listen())
afterEach(() => server.resetHandlers())
afterAll(() => server.close())

it('renders default children when accessed', () => {
    render(<ShortenUrlForm />);
    expect(screen.getByText('URL')).toBeDefined
    expect(screen.getByTestId('url-input')).toBeDefined
    expect(screen.getByTestId('shorten-button-input')).toBeDefined
});

it('it validates url input by disabling the button', async () => {
    const user = userEvent.setup()
    render(<ShortenUrlForm />);
    const shortenButton = screen.getByTestId('shorten-button-input')

    expect(shortenButton).toBeDisabled()

    const urlInput = screen.getByTestId('url-input')
    await user.type(urlInput, 'www.google.com')
    expect(shortenButton).toBeEnabled()
});

it('button is disabled while the input is not correct', async () => {
    const user = userEvent.setup()
    render(<ShortenUrlForm />);
    const shortenButton = screen.getByTestId('shorten-button-input')

    const urlInput = screen.getByTestId('url-input')
    await user.type(urlInput, 'www')

    expect(shortenButton).toBeDisabled()

    await user.type(urlInput, 'http://')
    expect(shortenButton).toBeDisabled()

    await user.type(urlInput, 'www.google')
    expect(shortenButton).toBeEnabled()
});

it('show success dialog with shortened url when button is pressed', async () => {
    const user = userEvent.setup()
    render(<ShortenUrlForm />);
    server.use(
        http.post('/shorten', (ctx) => {
            return HttpResponse.json({shortened_url: 'http://www.example.com/goto/jsjsjsj'})
        })
    )
    const shortenButton = screen.getByTestId('shorten-button-input')

    const urlInput = screen.getByTestId('url-input')
    await user.type(urlInput, 'www.example.com')

    await user.click(shortenButton)
    expect(await screen.findByTestId('success-dialog')).toBeDefined
});
