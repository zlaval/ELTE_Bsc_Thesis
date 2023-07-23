<template>
  <div class="flex justify-center mt-5">
    <h1 class="page-title">Új videó feltöltése</h1>
  </div>

  <v-form
      class="m-6"
      v-model="valid"
      validate-on="submit"
      @submit.prevent="submit"
  >
    <v-text-field
        v-model="video.title"
        label="Név"
        required
        :rules="ValidationRules.requiredRule"
    ></v-text-field>

    <v-file-input
        label="Videó"
        accept="video/*,.mkv,.avi"
        @change="onFileChange"

        hint="Maximum 2GB avi,mkv vagy mp4 fájl"
    ></v-file-input>
    <v-input :rules="videoRule"></v-input>

    <div class="flex justify-start">
      <v-switch
          label="Publikus"
          v-model="video.public"
          color="indigo-darken-3"
      ></v-switch>
    </div>


    <v-btn
        prepend-icon="mdi-content-save-outline"
        color="success"
        type="submit"
    >Mentés
    </v-btn>

  </v-form>

</template>

<script>
import {ValidationRules} from "@/util/utils";

export default {
  name: "VideoEditorView",
  computed: {
    ValidationRules() {
      return ValidationRules
    },
    videoRule() {
      return [
        () => (this.video.file) || "Kérem töltsön fel egy videó fájlt."

      ]
    }
  },
  data() {
    return {
      video: {
        title: null,
        file: null,
        public: false,
      },
      valid: true,
    }
  },
  methods: {
    onFileChange(file) {
      this.video.file = file.target.files[0]
    },
    async submit(event) {
      await event
      if (this.valid) {
        this.$rest.saveVideo(this.video, () => {
          this.$emit("actionSuccess")
          this.$router.push(`/video`)
        })
      }
    }
  }
}
</script>

<style scoped>

</style>