<template>
  <v-dialog
      v-model="dialog"
      width="400px"
  >
    <v-card class="py-7">
      <v-card-title>{{ title }}</v-card-title>
      <v-card-text>
        {{ message }}
      </v-card-text>
      <v-card-item class="flex justify-end">
        <v-btn
            :prepend-icon="successIcon"
            color="primary"
            @click="confirmPressed()"
            class="mr-5 mb-2"
        >{{ successButton }}
        </v-btn>
        <v-btn
            prepend-icon="mdi-cancel"
            color="secondary"
            @click="cancelDialog()"
            class="mb-2"
        >Mégsem
        </v-btn>
      </v-card-item>
    </v-card>

  </v-dialog>
</template>

<script>
export default {
  name: "ConfirmDialog",
  props: [
    'model',
    'title',
    'message',
    'successButton',
    'successIcon',
    'successCallback',
    'closeConfirmCallback'
  ],
  methods: {
    cancelDialog() {
      this.closeConfirmCallback()
    },
    confirmPressed() {
      this.successCallback()
      this.closeConfirmCallback()
    }
  },
  data() {
    return {
      dialog: false
    }
  },
  watch: {
    model: function () {
      this.dialog = this.model
    }
  },
  mounted() {
    this.dialog = this.model
  }
}
</script>

<style scoped>

</style>