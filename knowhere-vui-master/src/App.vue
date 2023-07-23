<template>
  <v-app>
    <v-app-bar color="blue-darken-4">
      <img src="@/assets/knowhere-logo.png" style="height: 60px">
    </v-app-bar>
    <v-navigation-drawer permanent class="menu-bar">
      <MainMenu
          :userLoggedIn="loggedIn"
          @logout-event="logoutListener"
      ></MainMenu>
    </v-navigation-drawer>
    <v-main>
      <v-container>

        <v-alert
            closable
            v-model="error"
            title="Hiba történt"
            type="error"
        >
          <span v-html="errorMessage"></span>
        </v-alert>

        <div class="flex justify-start ml-5" v-if="this.$route.path !=='/'">
          <a @click="$router.go(-1)" class="font-medium text-blue-600 cursor-pointer  hover:text-blue-800">

            <v-icon icon="mdi-arrow-left"></v-icon>
            Vissza
          </a>
        </div>

        <router-view
            :key="$route.path"
            @login-event="loginListener"
            @action-success="actionSuccess"
        >
        </router-view>

        <v-overlay
            v-model="overlay"
            class="justify-center align-center"
            scroll-strategy="block"
            contained
        >
          <div class="ml-64">
            <v-progress-circular
                color="primary"
                indeterminate
                :size="45"
                :width="6"
            >
            </v-progress-circular>
          </div>
        </v-overlay>

        <v-snackbar
            v-model="snackbar"
            timeout="3000"
            color="deep-purple-accent-4"
        >
          <div class="w-full flex justify-center">
            A művelet végrehajtása sikeres!
          </div>
        </v-snackbar>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>


import MainMenu from "@/components/MainMenu";
import * as jose from "jose";

export default {
  name: 'App',
  components: {
    MainMenu
  },
  data() {
    return {
      loggedIn: "0",
      overlay: false,
      errorMessage: null,
      error: false,
      snackbar: false,
    }
  },
  methods: {
    getUserName() {
      return localStorage.getItem('userName')
    },
    actionSuccess() {
      this.snackbar = true
    },
    logoutListener() {
      localStorage.removeItem('jwtToken')
      localStorage.removeItem('userName')
      localStorage.removeItem('userId')
      this.loggedIn = "0"
    },
    loginListener(token) {
      localStorage.setItem('jwtToken', token)
      const claims = jose.decodeJwt(token)
      localStorage.setItem('userName', claims.USER_NAME)
      localStorage.setItem('userId', claims.USER_ID)
      this.loggedIn = "1"
      this.$forceUpdate()
    },
    checkLogin() {
      const token = localStorage.getItem('jwtToken')
      this.loggedIn = "0"
      if (token) {
        const claims = jose.decodeJwt(token)
        const expiry = claims.exp
        const now = new Date().getTime() / 1000
        if (expiry < now) {
          localStorage.removeItem('jwtToken')
          localStorage.removeItem('userName')
          this.$router.push("/login")
        } else {
          const path = claims.ROLE.replace('ROLE_', '').toLowerCase()
          this.$router.push(`/${path}`)
          this.loggedIn = "1"
        }
      } else {
        this.$router.push("/")
      }
    },
    handleError(error) {
      if (!error) {
        return
      }
      console.log(error)
      if (!error.response) {
        if (error.config.url.includes("notification")) return
        this.errorMessage = "A szerver nem tudja kiszolgálni a kérést. Kérem próbálja meg később."
      } else if (
          error.response &&
          error.response.data &&
          (
              (error.response.data.url &&
                  error.response.data.url.includes("notification")
              ) || (
                  error.response.data.path &&
                  error.response.data.path.includes("notification")
              )
          )
      ) {
        return
      } else if (error.response.status === 500) {
        this.errorMessage = "A szerver nem tudja kiszolgálni a kérést. Kérem próbálja meg később."
      } else {
        if (error.response && error.response.data) {
          const errorData = error.response.data
          if (errorData.title) {
            let errors = errorData.title
            if (errorData.invalid_fields) {
              Object.values(errorData.invalid_fields).forEach((msg) => {
                errors += `</br>○ ${msg}`
              })
            }
            this.errorMessage = errors
          } else {
            this.errorMessage = errorData
          }

        } else {
          this.errorMessage = error.message
        }

      }
      this.error = true
      this.overlay = false
    },
    addInterceptors() {
      this.$axios.interceptors.request.use(
          (config) => {
            console.log(config)
            if (!config.url.includes("notification"))
              this.overlay = true
            return config
          },
          (error) => {
            this.handleError(error)
            return Promise.reject(error)
          }
      )
      this.$axios.interceptors.response.use(
          (response) => {
            this.overlay = false
            return response
          },
          (error) => {
            this.handleError(error)
            return Promise.reject(error)
          }
      )
    }
  },
  created() {
    this.addInterceptors()
    this.checkLogin()
  }
}
</script>

<style>

.v-navigation-drawer__content {
  box-shadow: 5px 0 15px rgba(0, 0, 0, 0.5);
}


</style>
