import axios from "./axios";

const _rest = {}

const safeCallback = (callback, response) => {
    if (callback) {
        callback(response)
    }
}

const authApi = "/auth/api/v1"
const subjectApi = "/subject/api/v1"
const quizApi = "/quiz/api/v1"

_rest.loadAllUser = (callback) => {
    axios.get(`${authApi}/user`).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.saveUser = (user, callback) => {
    axios.post(`${authApi}/user`, user).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.updateUser = (user, modifyPassword, callback) => {
    if (!modifyPassword) {
        user.password = null
        user.confirmPassword = null
    }
    axios.put(`${authApi}/user/${user.id}`, user).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.deleteUser = (user, callback) => {
    axios.delete(`${authApi}/user/${user.id}`).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.login = (loginData, callback) => {
    axios.post(`${authApi}/login`, loginData).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.register = (registrationData, callback) => {
    axios.post(`${authApi}/register`, registrationData).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.saveQuiz = (quiz, callback) => {
    if (quiz.id) {
        axios.put(`${quizApi}/${quiz.id}`, quiz).then((response) => {
            safeCallback(callback, response)
        }).catch()
    } else {
        axios.post(quizApi, quiz).then((response) => {
            safeCallback(callback, response)
        }).catch()
    }
}

_rest.loadQuiz = (id, callback) => {
    axios.get(`${quizApi}/${id}`).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.archiveQuiz = (id, callback) => {
    axios.patch(`${quizApi}/${id}`).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.loadQuizzes = (callback) => {
    axios.get(quizApi).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.postSolution = (solution, callback) => {
    axios.post(`${quizApi}/solution`, solution).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.getSubjectForTeacher = (config, callback) => {
    axios.get(`${subjectApi}/teacher`, config).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.getSubjectsForSelect = (callback) => {
    axios.get(`${subjectApi}/teacher/select`).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.getSubjectWithGrade = (subjectId, callback) => {
    axios.get(`${subjectApi}/teacher/subject-grades/${subjectId}`).then((response) => {
        safeCallback(callback, response)
    }).catch()
}


_rest.publishSubject = (id, callback) => {
    axios.patch(`${subjectApi}/teacher/${id}/publish`).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.uploadSubjectCoverImage = (file, callback) => {
    const form = new FormData()
    form.append("coverImage", file.target.files[0])
    axios.post(
        `${subjectApi}/teacher/image`,
        form,
        {
            headers: {
                "Content-Type": "multipart/form-data"
            }
        }
    ).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.uploadProfilePicture = (file, callback) => {
    const form = new FormData()
    form.append("picture", file.target.files[0])
    axios.post(
        `${authApi}/user/me/image`,
        form,
        {
            headers: {
                "Content-Type": "multipart/form-data"
            }
        }
    ).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.changePassword = (body, callback) => {
    axios.patch(`${authApi}/user/me/password`, body).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.saveSubject = (subject, callback) => {
    const request = subject
    const id = subject.id

    if (id) {
        axios.put(
            `/subject/api/v1/teacher/${id}`,
            request
        ).then((response) => {
            safeCallback(callback, response)
        }).catch()
    } else {
        axios.post(
            `/subject/api/v1/teacher`,
            request,
        ).then((response) => {
            safeCallback(callback, response)
        }).catch()
    }
}
_rest.loadSubjectForStudent = (id, callback) => {
    axios.get(`${subjectApi}/student/${id}`).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.loadEnrolledSubjectDetail = (id, callback) => {
    axios.get(`${subjectApi}/student/enrolled/${id}`).then((response) => {
        safeCallback(callback, response)
    }).catch()
}


_rest.loadSubjectForTeacher = (id, callback) => {
    axios.get(`/subject/api/v1/teacher/${id}`).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.loadEnrolledForStudent = (callback) => {
    axios.get(`/subject/api/v1/student`).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.loadFinishedForStudent = (config, callback) => {
    axios.get(`/subject/api/v1/student`, config).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.loadEnrollableSubjects = (callback) => {
    axios.get(`/subject/api/v1/student/available`).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.enroll = (subjectId, callback) => {
    axios.post(
        `/enrollment/api/v1/${subjectId}`,
    ).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.dropEnrollment = (subjectId, callback) => {
    axios.delete(
        `/enrollment/api/v1/${subjectId}`,
    ).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.getLessonForTeacher = (id, subjectId, callback) => {
    axios.get(`${subjectApi}/teacher/${subjectId}/lesson/${id}`).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.getLessonForStudent = (id, callback) => {
    axios.get(`${subjectApi}/student/lesson/${id}`).then((response) => {
        safeCallback(callback, response)
    }).catch()
}


_rest.saveLesson = (subjectId, lesson, callback) => {
    if (lesson.id) {
        axios.put(`${subjectApi}/teacher/${subjectId}/lesson/${lesson.id}`, lesson).then((response) => {
            safeCallback(callback, response)
        }).catch()
    } else {
        axios.post(`${subjectApi}/teacher/${subjectId}/lesson`, lesson).then((response) => {
            safeCallback(callback, response)
        }).catch()
    }
}

_rest.archiveSubject = (id, callback) => {
    axios.patch(`${subjectApi}/teacher/${id}/archive`).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.unArchiveSubject = (id, callback) => {
    axios.patch(`${subjectApi}/teacher/${id}/unarchive`).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.studentsForSubject = (id, callback) => {
    axios.get(`${subjectApi}/teacher/${id}/students`).then((response) => {
        safeCallback(callback, response)
    }).catch()
}


_rest.deleteLesson = (subjectId, id, callback) => {
    axios.delete(`${subjectApi}/teacher/${subjectId}/lesson/${id}`).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.getMyGrades = (callback) => {
    axios.get(`/grade/api/v1`).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.loadMyUserData = (callback) => {
    axios.get(`/auth/api/v1/user/me`).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.archiveVideo = (id, callback) => {
    axios.get(`/video/api/v1/archive/${id}`).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.loadVideoData = (id, callback) => {
    axios.get(`/video/api/v1/${id}`).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.saveVideo = (video, callback) => {
    const form = new FormData()
    form.append("file", video.file)
    form.append("title", video.title)
    form.append("public", video.public)
    axios.post(
        `/video/api/v1`,
        form,
        {
            headers: {
                "Content-Type": "multipart/form-data"
            }
        }
    ).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.loadVideos = (config, callback) => {
    axios.get('/video/api/v1', config).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.loadPublicVideos = (config, callback) => {
    axios.get('/video/api/v1/public', config).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.loadVideoSelectData = (callback) => {
    axios.get('/video/api/v1/video-select').then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.getMyNotifications = (config, callback) => {
    axios.get('/notification/api/v1', config).then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.getMyNotificationCount = (callback) => {
    axios.get('/notification/api/v1/count').then((response) => {
        safeCallback(callback, response)
    }).catch()
}

_rest.readNotification = (id, callback) => {
    axios.patch(`/notification/api/v1/${id}`).then((response) => {
        safeCallback(callback, response)
    }).catch()
}


export default _rest