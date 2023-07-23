import {createApp} from 'vue'
import App from './App.vue'
import './index.css'
import {createRouter, createWebHistory} from "vue-router";
import LandingView from "@/view/common/LandingView";
import _axios from './plugins/axios'
import LoginView from "@/view/common/LoginView.vue";
import StudentHomeView from "@/view/student/HomeView.vue";
import AdminHomeView from "@/view/admin/HomeView.vue";
import TeacherHomeView from "@/view/teacher/HomeView.vue";
import TeacherSubjectDetailView from "@/view/teacher/SubjectDetailView.vue";
import RegistrationView from "@/view/common/RegistrationView.vue";
import TeacherClassDetailView from "@/view/teacher/ClassDetailView.vue";
import {createVuetify} from "vuetify";
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import 'vuetify/styles'
import {aliases, mdi} from 'vuetify/iconsets/mdi'
import '@mdi/font/css/materialdesignicons.css'
import VideoView from "@/view/teacher/VideoView.vue";
import VideoDetailView from "@/view/teacher/VideoDetailView.vue";
import VideoEditorView from "@/view/teacher/VideoEditorView.vue";
import QuizListView from "@/view/teacher/QuizListView.vue";
import QuizEditorView from "@/view/teacher/QuizEditorView.vue";
import SubjectView from "@/view/student/SubjectView.vue";
import LessionView from "@/view/student/LessionView.vue";
import _rest from "@/plugins/rest";
import SubjectGradesView from "@/view/teacher/SubjectGradesView.vue";
import SubjectsView from "@/view/student/SubjectsView.vue";
import EnrollmentView from "@/view/student/EnrollmentView.vue";
import GradesView from "@/view/student/GradesView.vue";
import NotificationView from "@/view/student/NotificationView.vue";


const app = createApp(App)

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            component: LandingView
        },
        {
            path: '/login/:tenant?',
            component: LoginView,
            props: true
        },
        {
            path: '/register',
            component: RegistrationView
        },
        {
            path: '/student',
            component: StudentHomeView
        },
        {
            path: '/admin',
            component: AdminHomeView
        },
        {
            path: '/teacher',
            component: TeacherHomeView
        },
        {
            path: '/video',
            component: VideoView
        },
        {
            path: '/teacher-subject-detail/:id?',
            component: TeacherSubjectDetailView,
            props: true
        },
        {
            path: '/teacher-subject-detail/:subjectId/lesson/:id?',
            component: TeacherClassDetailView,
            props: true
        },
        {
            path: '/video-detail/:id',
            component: VideoDetailView,
            props: true
        },
        {
            path: '/video-editor/:id?',
            component: VideoEditorView,
            props: true
        },
        {
            path: '/quiz-list/',
            component: QuizListView,
        },
        {
            path: '/quiz-editor/:id?',
            component: QuizEditorView,
            props: true
        },
        {
            path: '/student-subject-detail/:id',
            component: SubjectView,
            props: true
        },
        {
            path: '/student-class-detail/:id',
            component: LessionView,
            props: true
        },
        {
            path: '/subject-grades',
            component: SubjectGradesView,
        },
        {
            path: '/subjects',
            component: SubjectsView,
        },
        {
            path: '/enroll-subject/:id',
            component: EnrollmentView,
            props: true
        },
        {
            path: '/student-grades',
            component: GradesView,
        },
        {
            path: '/notifications',
            component: NotificationView,
        },
    ]
})

app.config.globalProperties.$axios = _axios
app.config.globalProperties.$rest = _rest
app.use(router)

const vuetify = createVuetify({
    components,
    directives,
    icons: {
        defaultSet: 'mdi',
        aliases,
        sets: {
            mdi,
        }
    },
})
app.use(vuetify)

app.mount('#app')
