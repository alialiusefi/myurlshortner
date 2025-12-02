export const buildUserIdHeader = (userId: number) => {
  return { "User-Id": userId.toString() };
};
