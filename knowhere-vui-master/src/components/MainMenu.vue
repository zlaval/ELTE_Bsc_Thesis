<template>
  <v-list>
    <v-list-item v-if="loggedIn"
                 :key="profilePicture"
                 :prepend-avatar="getAvatarUrl()"
                 :title="userName"
                 :subtitle="email"
                 @click="openDialog()"
    ></v-list-item>
  </v-list>

  <v-divider></v-divider>

  <v-list density="comfortable" nav class="text-xl " active-color="primary">
    <MenuItem
        icon="mdi-account-plus-outline"
        route="/register"
        title="Regisztráció"
        v-if="!this.loggedIn"
    ></MenuItem>

    <MenuItem
        icon="mdi-login"
        route="/login"
        title="Bejelentkezés"
        v-if="!this.loggedIn"
    ></MenuItem>
    <MenuItem
        icon="mdi-home-outline"
        route="/admin"
        title="Kezdőlap"
        v-if="hasRight('ADMIN')"
    ></MenuItem>
    <MenuItem
        icon="mdi-home-outline"
        route="/teacher"
        title="Kezdőlap"
        v-if="hasRight('TEACHER')"
    ></MenuItem>
    <MenuItem
        icon="mdi-home-outline"
        route="/student"
        title="Kezdőlap"
        v-if="hasRight('STUDENT')"
    ></MenuItem>
    <MenuItem
        icon="mdi-alpha-t-box-outline"
        route="/subjects"
        title="Tantárgyak"
        v-if="hasRight('STUDENT')"
    ></MenuItem>
    <MenuItem
        icon="mdi-video-outline"
        route="/video"
        title="Videók"
        v-if="hasRight('TEACHER')"
    ></MenuItem>
    <MenuItem
        icon="mdi-help-box-outline"
        route="/quiz-list"
        title="Kvízek"
        v-if="hasRight('TEACHER')"
    ></MenuItem>
    <MenuItem
        icon="mdi-clipboard-list-outline"
        route="/subject-grades"
        title="Jegyek"
        v-if="hasRight('TEACHER')"
    ></MenuItem>
    <MenuItem
        icon="mdi-clipboard-list-outline"
        route="/student-grades"
        title="Jegyek"
        v-if="hasRight('STUDENT')"
    ></MenuItem>
    <MenuItem
        icon="mdi-bell-outline"
        route="/notifications"
        title="Értesítések"
        v-if="hasRight('STUDENT')"
        :count="count"
        :show-count="true"
    ></MenuItem>
    <MenuItem
        @click="logout"
        icon="mdi-logout"
        route="/"
        title="Kijelentkezés"
        v-if="this.loggedIn"
    ></MenuItem>
  </v-list>

  <v-dialog
      v-model="currentUserDialog"
      v-if="user"
      width="600px"
  >


    <v-card>
      <div class="flex justify-end w-full mt-5 pr-5">
        <v-btn
            icon="mdi-window-close"
            variant="outlined"
            size="x-small"
            @click="currentUserDialog=false"
        ></v-btn>

      </div>
      <div class="py-5">
        <v-card-title>{{ user.name }}</v-card-title>
        <v-card-item class="m-3">
          <div class="flex justify-center m-5">
            <img class="thumbnail" :src="getAvatarUrl()">
          </div>
          <v-file-input
              @change="uploadImage"
              accept="image/*"
              label="Profil kép"
          ></v-file-input>
        </v-card-item>

        <v-card-item class="m-3">
          <v-switch
              label="Jelszó módosítása"
              v-model="modifyPassword"
              color="indigo-darken-3"
          ></v-switch>

          <v-form
              class="m-6"
              ref="form"
              v-model="valid"
              validate-on="submit"
              @submit.prevent="submit"
              v-if="modifyPassword"
          >

            <v-text-field
                v-model="passwordStore.currentPassword"
                label="Jelenlegi jelszó"
                :append-icon="show3 ? 'mdi-eye' : 'mdi-eye-off'"
                :type="show3 ? 'text' : 'password'"
                name="currentPassword"
                counter
                @click:append="show3 = !show3"
                :rules="ValidationRules.requiredRule"
            ></v-text-field>

            <v-text-field
                v-model="passwordStore.password"
                label="Jelszó"
                :append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'"
                :type="show1 ? 'text' : 'password'"
                name="password"
                hint="Legalább 8 karakter"
                counter
                @click:append="show1 = !show1"
                :rules="passwordRule"
            ></v-text-field>

            <v-text-field
                v-model="passwordStore.confirmPassword"
                label="Jelszó megerősítése"
                :append-icon="show2 ? 'mdi-eye' : 'mdi-eye-off'"
                :type="show2 ? 'text' : 'password'"
                name="confirmPassword"
                hint="Legalább 8 karakter"
                counter
                @click:append="show2 = !show2"
                :rules="passwordRule"
            ></v-text-field>

            <v-btn
                prepend-icon="mdi-content-save-outline"
                color="blue"
                type="submit"
            >Mentés
            </v-btn>

          </v-form>

        </v-card-item>
      </div>
    </v-card>

  </v-dialog>

</template>

<script>
import MenuItem from "@/components/MenuItem";
import * as jose from "jose";
import axios from "axios";
import {ValidationRules} from "@/util/utils";

export default {
  name: "MainMenu",
  components: {MenuItem},
  props: ["userLoggedIn"],
  computed: {
    ValidationRules() {
      return ValidationRules
    },
    passwordRule() {
      if (this.modifyPassword) {
        return ValidationRules.passwordRules.concat(
            () => (this.passwordStore.password === this.passwordStore.confirmPassword) || "A jelszó nem egyezik"
        )
      } else {
        return [() => true]
      }
    }
  },
  data() {
    return {
      currentUserDialog: false,
      loggedIn: false,
      role: null,
      userName: null,
      email: null,
      count: 0,
      interval: null,
      user: null,
      profilePicture: null,
      modifyPassword: false,
      passwordStore: {
        currentPassword: null,
        password: null,
        confirmPassword: null
      },
      show1: false,
      show2: false,
      show3: false,
      valid: true
    }
  },
  watch: {
    userLoggedIn: function () {
      this.loginEvent()
      this.loadUser()
      this.$forceUpdate()
    },
    modifyPassword: function () {
      this.passwordStore = {
        currentPassword: null,
        password: null,
        confirmPassword: null
      }
    }
  },
  methods: {
    openDialog() {
      this.modifyPassword = false
      this.currentUserDialog = true
    },
    loadUser() {
      if (this.loggedIn) {
        this.$rest.loadMyUserData((response) => {
          this.user = response.data
          this.profilePicture = this.user.picture
        })
      }
    },
    uploadImage(file) {
      this.$rest.uploadProfilePicture(file, (response) => {
        this.user.picture = response.data
        this.profilePicture = this.user.picture
        if (!this.modifyPassword) {
          this.currentUserDialog = false
        }
      })
    },
    async submit(event) {
      await event
      if (this.valid) {
        this.$rest.changePassword(this.passwordStore, () => {
          this.currentUserDialog = false
        })
      }
    },
    getAvatarUrl() {
      if (this.profilePicture) {
        return `${axios.defaults.baseURL}/auth/resource/image/${this.profilePicture}`
      } else {
        return "https://randomuser.me/api/portraits/men/20.jpg"
      }
    },
    getNotificationCount() {
      if (this.hasRight("STUDENT")) {
        this.$rest.getMyNotificationCount((response) => {
          this.count = response.data
        })
      }
    },
    logout() {
      this.removeInterval()
      this.loggedIn = false
      this.role = null
      this.$emit("logoutEvent")
      this.$forceUpdate()
    },
    hasRight(role) {
      return role === this.role
    },
    loginEvent() {
      const token = localStorage.getItem('jwtToken')
      if (token) {
        const claims = jose.decodeJwt(token)
        this.role = claims.ROLE.replace('ROLE_', '')
        this.email = claims.EMAIL
        this.userName = localStorage.getItem("userName")
        this.loggedIn = true
        this.watchNotifications()
      }
    },
    removeInterval() {
      console.log(`Clear interval ${this.interval}`)
      clearInterval(this.interval)
    },
    watchNotifications() {
      this.removeInterval()
      if (this.hasRight("STUDENT")) {
        this.interval = setInterval(this.getNotificationCount, 10000)
        console.log(`New interval ${this.interval}`)
      }
    }
  },
  created() {
    this.loginEvent()
  },
  mounted() {
    if (this.hasRight("STUDENT")) {
      this.getNotificationCount()
      this.watchNotifications()
    }
    this.loadUser()


  }
}
</script>

<style scoped>
.thumbnail {
  @apply w-56
}
</style>