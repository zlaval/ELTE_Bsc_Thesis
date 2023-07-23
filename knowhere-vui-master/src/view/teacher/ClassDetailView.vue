<template>
  <h1 class="page-title" v-if="lesson.id!==null">{{ lesson.name }}</h1>
  <h1 class="page-title" v-if="lesson.id===null">Új óra</h1>

  <v-form
      ref="form"
      v-model="valid"
      lazy-validation
      validate-on="submit"
      @submit.prevent="submit"
  >
    <div class="grid grid-cols-2 gap-3">
      <div>
        <v-text-field
            v-model="lesson.name"
            label="Név"
            required
            :rules="ValidationRules.requiredRule"
        >
        </v-text-field>
      </div>
      <div>
        <iso8601-date-time-picker
            v-model="lesson.deadLine"
            label="Határidő"
            :key="lesson.deadLine"
            :required="true"
        ></iso8601-date-time-picker>
      </div>
    </div>

    <v-textarea
        label="Leírás"
        v-model="lesson.description"
        :rules="ValidationRules.requiredRule"
    ></v-textarea>


    <div class="grid grid-cols-2 gap-3">
      <div>
        <v-select
            v-model="lesson.videoId"
            :items="videos"
            item-title="name"
            item-value="id"
            label="Video"
            :rules="ValidationRules.requiredRule"
        ></v-select>
        <img class="thumbnail" v-if="lesson.videoId" :src="getThumbNail(lesson.videoId)">
      </div>
      <div>
        <v-select
            v-model="lesson.quizId"
            :items="quizzes"
            item-title="name"
            item-value="id"
            label="Kvíz"
            :rules="ValidationRules.requiredRule"
        ></v-select>
      </div>
    </div>
    <div class="flex gap-3 my-5">
      <v-btn
          prepend-icon="mdi-content-save-outline"
          color="success"
          type="submit"
      >
        Mentés
      </v-btn>

      <v-btn
          prepend-icon="mdi-delete-outline"
          color="error"
          v-if="lesson.id!==null"
          @click="confirmDelete()"
      >
        Törlés
      </v-btn>
    </div>
  </v-form>

  <confirm-dialog
      :model="deleteConfirmDialog"
      title="Óra törlése"
      message="Biztosan törli? Minden, az órához tartozó adat elveszik!"
      success-button="Rendben"
      success-icon="mdi-delete"
      :success-callback="deleteLesson"
      :close-confirm-callback="closeConfirm"
  ></confirm-dialog>

</template>

<script>
import {useRoute} from "vue-router";
import axios from "axios";
import ConfirmDialog from "@/components/ConfirmDialog.vue";
import Iso8601DateTimePicker from "@/components/Iso8601DateTimePicker.vue";
import {ValidationRules} from "@/util/utils";

export default {
  name: "ClassDetailView",
  computed: {
    ValidationRules() {
      return ValidationRules
    }
  },
  components: {Iso8601DateTimePicker, ConfirmDialog},
  data() {
    return {
      deleteConfirmDialog: false,
      id: null,
      subjectId: null,
      videos: [],
      quizzes: [],
      lesson: {
        id: null,
        name: null,
        description: null,
        videoId: null,
        quizId: null,
        deadLine: null
      },
      valid: true,
    }
  },
  methods: {
    deleteLesson() {
      this.$rest.deleteLesson(this.subjectId, this.lesson.id, () => {
        this.deleteConfirmDialog = false
        this.$router.push(`/teacher-subject-detail/${this.subjectId}`)
      })
    },
    closeConfirm() {
      this.deleteConfirmDialog = false
    },
    confirmDelete() {
      this.deleteConfirmDialog = true
    },
    async submit(event) {
      await event
      if (this.valid) {
        this.$rest.saveLesson(this.subjectId, this.lesson, () => {
          this.$router.push(`/teacher-subject-detail/${this.subjectId}`)
        })
      }
    },
    getThumbNail(id) {
      const video = this.videos.find(i => i.id === id)
      if (video && video.thumbnail) {
        const img = video.thumbnail
        return `${axios.defaults.baseURL}/video/resource/image/${img}`
      } else {
        return "@/assets/video-placeholder.png"
      }
    },
    loadData() {
      if (this.id) {
        this.$rest.getLessonForTeacher(this.id, this.subjectId, (response) => {
          this.lesson = response.data
        })
      }
    },
    loadQuizzes() {
      this.$rest.loadQuizzes((response) => {
        this.quizzes = response.data.map((q) => (
            {
              id: q.id,
              name: q.name
            }
        ))
      })
    },
    loadVideos() {
      this.$rest.loadVideoSelectData((response) => {
        this.videos = response.data
      })
    }
  },
  mounted() {
    const route = useRoute()
    this.id = route.params.id
    this.subjectId = route.params.subjectId
    this.loadData()
    this.loadVideos()
    this.loadQuizzes()

  }
}
</script>

<style scoped>

.thumbnail {
  @apply w-1/12
}
</style>