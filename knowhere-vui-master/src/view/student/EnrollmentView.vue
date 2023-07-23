<template>
  <div class="flex justify-center">
    <div v-if="subject" class="w-2/3">


      <div class="flex justify-center">
        <h1 class="page-title" v-if="subject!==null">{{ subject.name }}</h1>
      </div>
      <div class="flex justify-center text-sm text-grey mb-10">
        <span>{{ getFormattedDate() }}</span>
      </div>

      <div class="border rounded p-5 text-lg mb-10 flex align-center">
        <div class="mr-5">
          <v-chip prepend-icon="mdi-credit-card-edit-outline">
            {{ subject.credit }}
          </v-chip>
        </div>
        <p>
          {{ subject.description }}
        </p>

      </div>
      <h2 class="text-xl font-semibold mt-5 mb-5">Órák:</h2>
      <div class="grid grid-cols-4 gap-4">
        <v-card
            v-for="lesson in subject.lessons"
            :title="lesson.name"
            :text="lesson.description"
            :key="lesson.id"
            class="mb-5 border"
        >
        </v-card>
      </div>


      <div class="flex justify-end">
        <v-btn
            prepend-icon="mdi-book-open-page-variant-outline"
            color="blue"
            class="mt-5"
            @click="enroll"
        >
          Tantárgy felvétele
        </v-btn>

      </div>


    </div>
  </div>
</template>

<script>
import {useRoute} from "vue-router";
import {formatDateHu} from "@/util/utils";

export default {
  name: "EnrollmentView",
  data() {
    return {
      id: null,
      subject: null
      //desc
      //credit
      //seats
      //start
      //end
      //lessons
    }
  },
  methods: {
    loadData() {
      this.$rest.loadSubjectForStudent(this.id, (response) => {
        this.subject = response.data
      })
    },
    getFormattedDate() {
      const start = formatDateHu(this.subject.startDt)
      let end = 'Korlátlan'
      if (this.subject.endDt) {
        end = formatDateHu(this.subject.endDt)
      }
      return `${start} - ${end}`
    },
    enroll() {
      this.$rest.enroll(this.subject.id, () => {
        this.$router.go(-1)
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