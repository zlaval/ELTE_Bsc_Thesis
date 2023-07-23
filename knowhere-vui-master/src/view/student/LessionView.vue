<template>
  <div v-if="lesson">
    <div class="flex justify-center mt-5">
      <h1 class="page-title">{{ lesson.name }}</h1>
    </div>

    <div class="flex justify-center my-5" v-if="video">
      <div class="w-3/4">
        <vue-video-player :video="video" :autoplay="false"></vue-video-player>
      </div>
    </div>

    <div class="flex justify-center my-5">
      <div class="w-3/4">
        <v-expansion-panels>
          <v-expansion-panel
              v-if="lesson.description"
              title="Leírás"
              :text="lesson.description"
          >
          </v-expansion-panel>
          <v-expansion-panel
              v-if="quiz"
              :title="quiz.name"
              :text="quiz.description"
          >
            <v-expansion-panel-text>

              <v-card
                  v-for="question in quiz.questions"
                  :key="question.id"
                  :title="question.question"
                  class="mb-3"
              >
                <div class="flex justify-end mr-5">
                  <v-chip prepend-icon="mdi-electron-framework">
                    {{ question.points }}
                  </v-chip>
                </div>

                <v-list>
                  <v-list-item
                      v-for="answer in question.answers"
                      :key="answer.id"
                      :value="answer"
                      active-color="primary"
                      @click="onAnswer(question.id,answer.id)"
                  >
                    <v-list-item-title v-text="answer.text"></v-list-item-title>
                  </v-list-item>

                </v-list>

              </v-card>
              <div class="flex justify-end">
                <v-btn
                    class="m-3"
                    color="primary"
                    prepend-icon="mdi-send-circle-outline"
                    @click="sendAnswer"
                >
                  Beküldés
                </v-btn>
              </div>

            </v-expansion-panel-text>
          </v-expansion-panel>
        </v-expansion-panels>
      </div>
    </div>

  </div>

</template>

<script>
import {useRoute} from "vue-router";
import VueVideoPlayer from "@/components/VueVideoPlayer.vue";

export default {
  name: "LessionView",
  components: {VueVideoPlayer},
  data() {
    return {
      id: null,
      lesson: null,
      video: null,
      quiz: null,
      studentAnswer: new Map()
    }
  },
  methods: {
    sendAnswer() {
      const solution = {
        lessonId: this.lesson.id,
        quizId: this.quiz.id,
        answers: Array.from(this.studentAnswer, ([key, value]) => {
          return {
            questionId: key,
            answerId: value
          };
        })
      }

      console.log(solution)
      this.$rest.postSolution(solution, () => {
        this.$router.go(-1)
      })
    },
    onAnswer(questionId, answerId) {
      this.studentAnswer.set(questionId, answerId)
    },
    loadData() {
      this.$rest.getLessonForStudent(this.id, (response) => {
        this.lesson = response.data
        this.loadVideo(this.lesson.videoId)
        this.loadQuiz(this.lesson.quizId)
      })
    },
    loadVideo(id) {
      this.$rest.loadVideoData(id, (response) => {
        this.video = response.data
      })
    },
    loadQuiz(id) {
      //todo new rest for student quiz, enrolled and not have grade and started but before deadline
      this.$rest.loadQuiz(id, (response) => {
        this.quiz = response.data
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