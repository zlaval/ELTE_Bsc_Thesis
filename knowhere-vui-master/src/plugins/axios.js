import axios from "axios";

window.axios = axios

if (window.location.href.indexOf('http://localhost:8080') === 0) {
    axios.defaults.baseURL = 'http://localhost:8000'
    //  axios.defaults.baseURL = 'http://localhost:80/api' //TODO rm this
} else if (window.location.href.indexOf('http://localhost:10000') === 0) {
    axios.defaults.baseURL = 'http://localhost:10001'
} else {
    const host = window.location.host
    const protocol = window.location.protocol
    const port = window.location.port
    let url = `${protocol}//${host}`
    if (port) {
        url += `:${port}`
    }
    url += "/api"
    axios.defaults.baseURL = url
}


axios.defaults.withCredentials = true
axios.interceptors.request.use((config) => {
    const token = localStorage.getItem('jwtToken')
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    if (!config.headers.Accept) {
        config.headers.Accept = 'application/json'
    }
    return config
})

export default axios

