export const TARGET_URL_REGEX = /(http(s)?:\/\/.)?(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&=]*)/g
export const UID_INPUT_REGEX = /^[a-zA-Z0-9-]{1,10}$/
export const USER_ID_REGEX = /^\d+$/

export const buildShortenUrlPath = () => "/"
export const buildInfoPagePath = (uid: string) => `/browse/${uid}/info`
export const buildBrowsePagePath = () => "/browse"

export const TITLE_ERROR_MESSAGE = "The provided title cannot exceed 100 characters"

export const EMPTY_VALUE = "<empty>"
