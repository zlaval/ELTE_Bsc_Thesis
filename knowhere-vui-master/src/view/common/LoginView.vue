<template>

  <div class="relative flex flex-col justify-center overflow-hidden">
    <v-card
        title="Bejelentkezés"
        subtitle="Bejelentkezés az intézmény oldalára"
        variant="outlined"
        class="w-full m-auto max-w-2xl"
    >

      <v-form
          class="m-6"
          ref="form"
          v-model="valid"
          validate-on="submit"
          @submit.prevent="submit"
          @keyup.enter="submit"
      >

        <v-text-field
            v-model="postData.tenantName"
            label="Intézmény Azonosító"
            ref="tenantInput"
            :rules="ValidationRules.instNameRule"
            required
        ></v-text-field>

        <v-text-field
            v-model="postData.email"
            label="Email"
            ref="emailInput"
            required
            :rules="ValidationRules.emailRules"
        ></v-text-field>

        <v-text-field
            v-model="postData.password"
            label="Jelszó"
            :append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'"
            :type="show1 ? 'text' : 'password'"
            name="password"
            @click:append="show1 = !show1"
            required
            :rules="ValidationRules.requiredRule"
        ></v-text-field>

        <v-btn
            prepend-icon="mdi-login"
            color="blue"
            class="mt-5"
            type="submit"
        > Bejelentkezés
        </v-btn>

      </v-form>
    </v-card>

  </div>
</template>

<script>

import * as jose from 'jose'
import {useRoute} from "vue-router";
import {ValidationRules} from "@/util/utils";

export default {
  name: "LoginView",
  computed: {
    ValidationRules() {
      return ValidationRules
    }
  },
  data() {
    return {
      valid: true,
      postData: {
        tenantName: null,
        email: null,
        password: null
      },
      show1: false,
      show2: false,
    }
  },
  methods: {
    async submit(event) {
      await event
      if (this.valid) {
        this.$rest.login(this.postData, (response) => {
          const token = response.data.token
          const claims = jose.decodeJwt(token)
          this.$emit("loginEvent", token)
          const path = claims.ROLE.replace('ROLE_', '').toLowerCase()
          this.$router.push(`/${path}`)
        })
      }
    }
  },
  mounted() {
    const route = useRoute()
    this.postData.tenantName = route.params.tenant
    if (this.postData.tenantName) {
      this.$refs.emailInput.focus()
    } else {
      this.$refs.tenantInput.focus()
    }

  }
}
</script>

<style scoped>

</style>