import http from 'k6/http';
import {check, sleep} from 'k6';
import {baseUrl} from "./env.js";

export let options = {
    vus: 50,
    duration: '10s'
}

export default function () {
    const params = {
        headers: {
            'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJST0xFIjoiUk9MRV9TVFVERU5UIiwiVEVOQU5UIjoiMSIsIlVTRVJfSUQiOiI0IiwiVVNFUl9OQU1FIjoiRUxURSBTdHVkZW50LTAiLCJFTUFJTCI6InN0dWRlbnQwQGVsdGUuaHUiLCJzdWIiOiJzdHVkZW50MEBlbHRlLmh1IiwiaWF0IjoxNjgwNjk3MjYxLCJleHAiOjE2ODA3ODM2NjF9.5nprd8vJmCs85FZx8ldqdS9_fw23pTt5YNM0Dil28PM'
        }
    }

    const res = http.get(`${baseUrl}/subject/api/v1/student`, params);
    check(res, {"status was 200": (r) => r.status === 200}, {'app': 'subject', 'user': 'student'});
    sleep(0.2);
}