<template>
  <div class="flex flex-col justify-center">

    <div class="flex justify-center">
      <h1 class="page-title">Felhasználók kezelése</h1>
    </div>
    <div class="flex justify-center mt-5 ">
      <div class="w-2/3">
        <div class="flex justify-end">
          <v-btn
              icon="mdi-plus"
              color="success"
              @click="addNewUser()">
          </v-btn>
        </div>

      </div>
    </div>

    <div class="flex justify-center ">
      <div class="w-2/3">
        <v-table class="mt-5 w-full">
          <thead>
          <tr>
            <th>Név</th>
            <th>Email</th>
            <th>Szerepkör</th>
            <th></th>
          </tr>

          </thead>
          <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.name }}</td>
            <td>{{ user.email }}</td>
            <td>{{ getRoleName(user.role) }}</td>
            <td>
              <v-btn
                  icon="mdi-pencil"
                  color="blue"
                  size="x-small"
                  @click="openEditUserDialog(user)"
                  class="mr-5 mb-2 mt-2"
              >
              </v-btn>
              <v-btn
                  icon="mdi-delete"
                  color="red"
                  size="x-small"
                  v-if="user.role !=='ROLE_ADMIN'"
                  @click="confirmDelete(user)"
                  class=" mb-2 mt-2"
              ></v-btn>
            </td>
          </tr>
          </tbody>
        </v-table>
      </div>
    </div>
  </div>


  <!--  Editor dialog -->

  <v-dialog
      v-model="userDialog"
      width="50vw"
  >

    <v-form
        class="m-6"
        ref="form"
        v-model="valid"
        validate-on="submit"
        @submit.prevent="submit"
    >

      <v-card class="py-7">
        <v-card-title v-if="editedUser.id">{{ editedUser.name }}</v-card-title>
        <v-card-title v-if="!editedUser.id">Új felhasználó</v-card-title>

        <v-card-item>

          <v-text-field
              v-model="editedUser.name"
              label="Név"
              required
              :rules="ValidationRules.requiredRule"
          ></v-text-field>
          <v-text-field
              v-model="editedUser.email"
              :rules="ValidationRules.emailRules"
              label="Email"
              required
          ></v-text-field>

          <v-select
              v-model="editedUser.role"
              :items="roles"
              item-title="name"
              item-value="id"
              label="Szerepkör"
              :rules="ValidationRules.requiredRule"
              hint="Válassza ki a felhasználó szerepkörét"
              required
          ></v-select>

          <v-switch
              v-if="editedUser.id"
              label="Jelszó módosítása"
              v-model="modifyPassword"
              color="indigo-darken-3"
          ></v-switch>

          <v-text-field
              v-if="modifyPassword || !editedUser.id"
              v-model="editedUser.password"
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
              v-if="modifyPassword || !editedUser.id"
              v-model="editedUser.confirmPassword"
              label="Jelszó megerősítése"
              :append-icon="show2 ? 'mdi-eye' : 'mdi-eye-off'"
              :type="show2 ? 'text' : 'password'"
              name="confirmPassword"
              hint="Legalább 8 karakter"
              counter
              @click:append="show2 = !show2"
              :rules="passwordRule"
          ></v-text-field>

        </v-card-item>

        <v-card-item class="flex justify-end">

          <v-btn
              prepend-icon="mdi-close-circle-outline"
              color="warning"
              @click="cancelUserDialog()"
              class="mr-5"
          >Mégsem
          </v-btn>

          <v-btn
              prepend-icon="mdi-content-save-outline"
              color="blue"
              type="submit"
          >Mentés
          </v-btn>

        </v-card-item>
      </v-card>

    </v-form>

  </v-dialog>

  <v-dialog
      v-model="dialog"
      width="30vw"
  >
    <v-card class="py-7">
      <v-card-title>{{ editedUser.name }}</v-card-title>
      <v-card-text>
        Törli a kiválasztott felhasználót?
      </v-card-text>
      <v-card-item class="flex justify-end">
        <v-btn
            prepend-icon="mdi-delete"
            color="red"
            @click="deleteUser()"
            class="mr-5 mb-3"
        >Törlés
        </v-btn>
        <v-btn
            prepend-icon="mdi-close-circle-outline"
            color="warning"
            @click="cancelDialog()"
            class="mb-3"
        >Mégsem
        </v-btn>
      </v-card-item>
    </v-card>

  </v-dialog>
</template>

<script>
import {ValidationRules} from "@/util/utils";

export default {
  name: "HomeView",
  computed: {
    ValidationRules() {
      return ValidationRules
    },
    passwordRule() {
      if ((this.editedUser.id && this.modifyPassword) || !this.editedUser.id) {
        return ValidationRules.passwordRules.concat(
            () => (this.editedUser.password === this.editedUser.confirmPassword) || "A jelszó nem egyezik"
        )
      } else {
        return [() => true]
      }
    }
  },
  methods: {
    addNewUser() {
      const user = {
        id: null,
        name: null,
        email: null,
        role: null,
        password: null,
        confirmPassword: null,
      }
      this.openEditUserDialog(user)
    },
    openEditUserDialog(user) {
      this.modifyPassword = false
      this.editedUser = user
      this.userDialog = true
    },
    confirmDelete(user) {
      this.editedUser = user
      this.dialog = true
    },
    cancelDialog() {
      this.dialog = false
    },
    cancelUserDialog() {
      this.userDialog = false
    },
    getRoleName(role) {
      return this.roles.filter((r) => r.id === role)[0].name
    },
    async submit(event) {
      await event
      if (this.valid) {
        if (this.editedUser.id) {
          this.$rest.updateUser(this.editedUser, this.modifyPassword, () => {
            this.userDialog = false
            this.$emit("actionSuccess")
          })
        } else {
          this.$rest.saveUser(this.editedUser, (response) => {
            this.users.push(response.data)
            this.userDialog = false
            this.$emit("actionSuccess")
          })
        }
      }
    },
    deleteUser() {
      this.$rest.deleteUser(this.editedUser, () => {
        this.users = this.users.filter((u) => u.id !== this.editedUser.id)
        this.dialog = false
        this.$emit("actionSuccess")
      })
    }
  },
  mounted() {
    this.$rest.loadAllUser((response) => {
      this.users = response.data
    })
  },
  data() {
    return {
      roles: [
        {id: "ROLE_ADMIN", name: "Admin"},
        {id: "ROLE_TEACHER", name: "Tanár"},
        {id: "ROLE_STUDENT", name: "Diák"},
      ],
      dialog: false,
      userDialog: false,
      editedUser: null,
      users: [],
      valid: true,
      show1: false,
      show2: false,
      modifyPassword: false,
    }
  }
}
</script>

<style scoped>

</style>