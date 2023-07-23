<template>
  <div class="flex justify-center">
    <h1 class="page-title" v-if="subject.id!==null">{{ subject.name }}</h1>
    <h1 class="page-title" v-if="subject.id===null">Új tantárgy</h1>
  </div>

  <div v-if="subject.published" class="flex justify-center mt-1">
    <h2 class="published">(Publikálva)</h2>
  </div>

  <div class="grid grid-cols-5 ">
    <div class="col-span-3 mx-2">

      <v-form
          class="m-6"
          ref="form"
          v-model="valid"
          validate-on="submit"
          @submit.prevent="submit"
      >

        <v-text-field
            v-model="subject.name"
            label="Név"
            required
            :rules="ValidationRules.requiredRule"
        >
        </v-text-field>

        <v-textarea
            label="Leírás"
            v-model="subject.description"
        ></v-textarea>

        <div class="grid grid-cols-2 gap-3">
          <v-text-field
              v-model.number="subject.credit"
              label="Kredit"
              type="number"
              required
              :rules="ValidationRules.positiveNumberRule"
          >
          </v-text-field>

          <v-text-field
              v-model.number="subject.seats"
              label="Férőhely"
              type="number"
              required
              :rules="ValidationRules.positiveNumberRule"
          >
          </v-text-field>
        </div>

        <div class="grid grid-cols-2 gap-3">
          <iso8601-date-time-picker
              v-model="subject.startDt"
              label="Kurzus kezdete"
              :key="subject.startDt"
              :required="true"
          ></iso8601-date-time-picker>

          <iso8601-date-time-picker
              v-model="subject.endDt"
              label="Kurzus vége"
              :key="subject.endDt"
          ></iso8601-date-time-picker>
        </div>

        <img class="thumbnail" v-if="subject.coverImagePath && subject.id" :src="getImageSource()">
        <v-file-input
            @change="selectFile"
            accept="image/*"
            label="Borítókép"
        ></v-file-input>

        <div class="flex gap-3">
          <v-btn
              prepend-icon="mdi-content-save-outline"
              color="success"
              type="submit"
          >Mentés
          </v-btn>

          <v-btn
              prepend-icon="mdi-content-save-outline"
              color="primary"
              @click="publish()"
              v-if="subject.id!==null && !subject.published"
          >Publikál
          </v-btn>

          <v-btn
              prepend-icon="mdi-archive"
              color="primary"
              v-if="subject.id!==null &&!subject.archive"
              @click="confirmArchive"
          >Archiválás
          </v-btn>

          <v-btn
              prepend-icon="mdi-archive"
              color="secondary"
              v-if="subject.id!==null && subject.archive"
              @click="unArchiveSubject"
          > Archiválás visszavonása
          </v-btn>

        </div>

      </v-form>

      <v-card title="Hallgatók" class="text-center mt-3">
        <div class="m-2 text-left"
             v-for="student in students"
             :key="student.id"
        >

          <v-card :title="student.name" variant="outlined">
            <v-progress-linear
                :model-value="calculateProgress(student)"
                :color="getProgressBarColor(student)"
                height="15"
            >
            </v-progress-linear>
          </v-card>

        </div>
      </v-card>

    </div>


    <div class="col-span-2">
      <div class="flex justify-end mb-4">
        <v-btn
            icon="mdi-plus"
            color="warning"
            size="large"
            @click="newClass"
            v-if="subject.id!==null"
        >
        </v-btn>
      </div>
      <v-timeline
          align="start"
      >
        <v-timeline-item
            v-for="lesson in subject.lessons"
            :key="lesson.id"
            dot-color="red"
            icon="mdi-vuetify"
            fill-dot
        >
          <v-card @click="routeToDetail(lesson.id)">
            <v-card-title class="bg-blue-600 text-white">
              {{ lesson.name }}
            </v-card-title>
            <v-card-text class="bg-white text--primary">
              <p class="py-3">
                {{ lesson.description }}
              </p>
            </v-card-text>
          </v-card>
        </v-timeline-item>

      </v-timeline>

    </div>

  </div>

  <confirm-dialog
      :model="archiveConfirmDialog"
      title="Tantárgy archiválása"
      message="Biztosan archíválni szeretné?"
      success-button="Rendben"
      success-icon="mdi-archive"
      :success-callback="archiveSubject"
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
  name: "SubjectDetailView",
  computed: {
    ValidationRules() {
      return ValidationRules
    }
  },
  components: {Iso8601DateTimePicker, ConfirmDialog},
  data() {
    return {
      id: null,
      archiveConfirmDialog: false,
      subject: {
        id: null,
        name: null,
        description: null,
        credit: null,
        seats: null,
        published: true,
        startDt: null,
        endDt: null,
        coverImagePath: null,
        lessons: [],
        occupiedSeats: null,
      },
      students: [],
      valid: true,
    }
  },
  methods: {
    closeConfirm() {
      this.archiveConfirmDialog = false
    },
    confirmArchive() {
      this.archiveConfirmDialog = true
    },
    archiveSubject() {
      this.$rest.archiveSubject(this.id, () => {
        this.$router.push(`/teacher`)
      })
    },
    unArchiveSubject() {
      this.$rest.unArchiveSubject(this.id, (response) => {
        this.subject = response.data
      })
    },
    getImageSource() {
      return `${axios.defaults.baseURL}/subject/resource/image/${this.subject.coverImagePath}`
    },
    publish() {
      this.$rest.publishSubject(this.subject.id, () => {
        this.subject.published = true
        this.$emit("actionSuccess")
      })
    },
    selectFile(file) {
      this.$rest.uploadSubjectCoverImage(file, (response) => {
        this.subject.coverImagePath = response.data
      })
    },
    newClass() {
      this.$router.push(`/teacher-subject-detail/${this.subject.id}/lesson`)
    },
    calculateProgress(student) {
      const val = 100 / this.subject.lessons.length * student.finishedLessons
      return val
    },
    getProgressBarColor(student) {
      const p = this.calculateProgress(student)
      if (p < 100) {
        return "light-blue"
      } else {
        return "green"
      }
    },
    routeToDetail(id) {
      this.$router.push(`/teacher-subject-detail/${this.subject.id}/lesson/${id}`)
    },
    async submit(event) {
      await event
      if (this.valid) {
        this.$rest.saveSubject(this.subject, (response) => {
          this.subject = response.data
          this.$emit("actionSuccess")
        })
      }
    },
    loadData() {
      this.$rest.loadSubjectForTeacher(this.id, (response) => {
        this.subject = response.data
      })
    },
    loadStudents() {
      this.$rest.studentsForSubject(this.id, (response) => {
        this.students = response.data
      })
    }
  },

  mounted() {
    const route = useRoute()
    this.id = route.params.id
    if (this.id) {
      this.loadData()
      this.loadStudents()
    }
  }
}
</script>

<style scoped>

container {
  width: 80%;
}

.thumbnail {
  @apply w-1/12 mb-4
}

.published {
  @apply text-lg text-gray-500 font-semibold
}
</style>