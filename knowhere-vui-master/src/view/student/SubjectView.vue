<template>
  <div v-if="subject">
    <div class="flex justify-center mt-5">
      <h1 class="page-title" v-if="subject.id!==null">{{ subject.name }}</h1>
    </div>

    <div class="flex align-center justify-center mb-10 mt-5">
      <div class="w-2/3 flex justify-end">
        <v-btn
            v-if="!isAnyClassFinished()"
            prepend-icon="mdi-minus-circle-outline"
            color="error"
            @click="dropStart()"
        >
          Tantárgy leadása
        </v-btn>
      </div>
    </div>

    <div class="text-lg mb-10 flex align-center justify-center">
      <p class="w-2/3 border rounded p-5 ">
        {{ subject.description }}
      </p>
    </div>

    <div class="flex justify-center mt-16">
      <v-timeline class="w-2/3">
        <v-timeline-item
            v-for="clazz in subject.lessons"
            :key="clazz.id"
            :dot-color="getColor(clazz)"
            icon="mdi-vuetify"
            fill-dot
            class="v-timeline-item__override"
        >

          <v-card
              @click="routeToClass(clazz.id)"
              :disabled="clazz.finished || deadLine(clazz)"
          >
            <v-card-title class="bg-blue-600 text-white">
              {{ clazz.name }}
            </v-card-title>
            <v-card-text class="bg-white text--primary">
              <p class="py-3">
                {{ clazz.description }}
              </p>
            </v-card-text>
            <div
                class="mb-2 mx-3 flex justify-between align-center">
              <div class="text-xs">Határidő: {{ formatDate(clazz.deadLine) }}</div>
              <div v-if="clazz.points!==null">
                <v-chip prepend-icon="mdi-electron-framework">
                  {{ clazz.points }} / {{ clazz.maxPoint }}
                </v-chip>
              </div>
            </div>
          </v-card>
        </v-timeline-item>
      </v-timeline>
    </div>

  </div>

  <confirm-dialog
      :model="dropConfirmDialog"
      title="Tantárgy leadása"
      message="Biztosan leadja a tantárgyat? A teljesített órákért járó pontjai elvesznek!"
      success-button="Lead"
      success-icon="mdi-minus-circle-outline"
      :success-callback="dropSubject"
      :close-confirm-callback="closeConfirm"
  ></confirm-dialog>

</template>

<script>
import {useRoute} from "vue-router";
import {formatDateHu} from "@/util/utils";
import ConfirmDialog from "@/components/ConfirmDialog.vue";

export default {
  name: "SubjectView",
  components: {ConfirmDialog},
  data() {
    return {
      id: null,
      subject: null,
      dropConfirmDialog: false
    }
  },
  methods: {
    dropSubject() {
      this.$rest.dropEnrollment(this.id, () => {
        this.dropConfirmDialog = false
        this.$router.go(-1)
      })
    },
    dropStart() {
      this.dropConfirmDialog = true
    },
    closeConfirm() {
      this.dropConfirmDialog = false
    },
    deadLine(lesson) {
      if (!lesson.deadLine) {
        return false
      }
      const end = new Date(lesson.deadLine)
      const now = new Date()
      return now > end

    },
    isAnyClassFinished() {
      if (this.subject) {
        return this.subject.lessons.some(c => c.finished)
      }
      return false
    },
    getColor(clazz) {
      if (clazz.finished) {
        return "red"
      } else {
        return "blue"
      }
    },
    formatDate(date) {
      if (date) {
        return formatDateHu(date)
      }
      return "nincs"
    },
    routeToClass(lessonId) {
      this.$router.push(`/student-class-detail/${lessonId}`)
    },
    loadData() {
      this.$rest.loadEnrolledSubjectDetail(this.id, (response) => {
        this.subject = response.data
      })
    }
  },
  mounted() {
    const route = useRoute()
    this.id = route.params.id
    if (this.id) {
      this.loadData()
    }
  }
}
</script>

<style scoped>

</style>