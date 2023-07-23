import http from 'k6/http';
import {check, sleep} from 'k6';
import {baseUrl} from "./env.js";


export let options = {
    stages: [
        {duration: '1m', target: '50'},
        {duration: '5m', target: '500'},
        {duration: '1m', target: '100'},
        {duration: '20s', target: '10'},
        {duration: '5s', target: '0'},
    ]
}

export default function () {
    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJST0xFIjoiUk9MRV9URUFDSEVSIiwiVEVOQU5UIjoiMSIsIlVTRVJfSUQiOiIxIiwiVVNFUl9OQU1FIjoiRUxURSBUZWFjaGVyLTAiLCJFTUFJTCI6InRlYWNoZXIwQGVsdGUuaHUiLCJzdWIiOiJ0ZWFjaGVyMEBlbHRlLmh1IiwiaWF0IjoxNjgwNzA4Njc2LCJleHAiOjE2ODA3OTUwNzZ9.IS0yEuht5UYGzVu1moXCqj-ZR-KdS54XK1fyO9qIJl0'
        }
    }

    const payload = JSON.stringify({
        name: 'k6 load test',
        description: 'Description',
        credit: 4,
        seats: 15,
        startDt: new Date()
    })

    const res = http.post(`${baseUrl}/subject/api/v1/teacher`, payload ,params);
    check(res, {"status was 201": (r) => r.status === 201}, {'app': 'subject', 'user': 'student'});
    sleep(1);
}