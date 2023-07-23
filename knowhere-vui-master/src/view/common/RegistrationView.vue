<template>

  <div class="relative flex flex-col justify-center overflow-hidden">
    <v-card
        title="Regisztráció"
        subtitle="Új intézmény regisztrálása"
        variant="outlined"
        class="w-full m-auto max-w-2xl"
    >
      <v-form
          class="m-6"
          ref="form"
          v-model="valid"
          validate-on="submit"
          @submit.prevent="submit"
      >

        <v-text-field
            v-model="registration.tenant"
            label="Intézmény Azonosító"
            hint="A bejelentkezéshez használt rövid intézmény név (pl: elte)"
            required
            :rules="ValidationRules.instNameRule"
        >
        </v-text-field>


        <v-text-field
            v-model="registration.displayName"
            label="Intézmény Neve"
            required
            hint="Az intérmény neve. Például 'Eötvös Loránd Tudományegyetem'"
            :rules="ValidationRules.requiredRule"
        ></v-text-field>

        <v-text-field
            v-model="registration.name"
            label="Felhasználó neve"
            :rules="ValidationRules.requiredRule"
        ></v-text-field>

        <v-text-field
            v-model="registration.email"
            :rules="ValidationRules.emailRules"
            label="Email"
            required
        ></v-text-field>

        <v-text-field
            v-model="registration.password"
            label="Jelszó"
            :append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'"
            :rules="ValidationRules.passwordRules.concat(passwordConfirmationRule)"
            :type="show1 ? 'text' : 'password'"
            name="password"
            hint="Legalább 8 karakter"
            counter
            @click:append="show1 = !show1"
            required
        ></v-text-field>

        <v-text-field
            v-model="registration.confirmPassword"
            label="Jelszó megerősítése"
            :append-icon="show2 ? 'mdi-eye' : 'mdi-eye-off'"
            :rules="ValidationRules.passwordRules"
            :type="show2 ? 'text' : 'password'"
            name="confirmPassword"
            hint="Legalább 8 karakter"
            counter
            @click:append="show2 = !show2"
            required
        ></v-text-field>


        <div class="flex justify-end">
          <v-btn
              prepend-icon="mdi-account-plus-outline"
              color="blue"
              class="mt-5"
              type="submit"
          > Regisztráció
          </v-btn>
        </div>
      </v-form>
    </v-card>
  </div>

</template>

<script>

import {ValidationRules} from "@/util/utils";

export default {
  name: "RegistrationView",
  computed: {
    ValidationRules() {
      return ValidationRules
    },
    passwordConfirmationRule() {
      return () => (this.registration.password === this.registration.confirmPassword) || "A jelszó nem egyezik"
    }
  },
  data() {
    return {
      valid: true,
      show1: false,
      show2: false,
      registration: {
        tenant: null,
        displayName: null,
        name: null,
        email: null,
        password: null,
        confirmPassword: null
      },
      enabled: false,
    }
  },
  methods: {
    async submit(event) {
      await event
      if (this.valid) {
        this.$rest.register(this.registration, (response) => {
          this.$router.push(`/login/${response.data}`)
        })
      }
    }

  }
}
</script>

<style scoped>

</style>