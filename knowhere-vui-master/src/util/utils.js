export function getCroppedText(text) {
    if (!text) {
        return null
    }
    if (text.length < 150) {
        return text
    }
    return text.substring(0, 150) + '...'
}

export function formatToHtmlDateTime(date) {
    if (date) {
        return date.substring(0, 16)
    }
    return null
}

export function formatToIso8601Date(date) {
    if (date) {
        return date + ":00Z"
    }
    return null
}

export function formatDateHu(date) {
    if (date) {
        return new Date(date).toLocaleDateString("hu-HU")
    }
    return null
}

export const ValidationRules = {
    emailRules: [
        v => !!v || 'Kötelező mező.',
        v => /.+@.+\..+/.test(v) || 'Hibás email cím.',
    ],
    passwordRules: [
        v => !!v || 'Kötelező mező.',
        v => v.length >= 8 || 'Minimum 8 karakter.',
    ],
    instNameRule: [
        v => !!v || 'Kötelező mező.',
        v => v.length >= 2 || 'Minimum 2 karakter.'
    ],
    requiredRule: [
        v => !!v || 'Kötelező mező.'
    ],
    positiveNumberRule: [
        v => !!v || 'Kötelező mező.',
        v => v > 0 || "Nullánál nagyobb szám."
    ]
}