<template>
  <div class="flex justify-center">
    <h1 class="page-title" v-if="quiz.id!==null">{{ quiz.name }}</h1>
    <h1 class="page-title" v-if="quiz.id===null">Új Kvíz</h1>
  </div>

  <div>


    <v-form
        ref="form"
        v-model="valid"
        validate-on="submit"
        @submit.prevent="submit"
    >

      <v-text-field
          v-model="quiz.name"
          label="Cím"
          required
          :rules="ValidationRules.requiredRule"
      >
      </v-text-field>

      <v-textarea
          label="Leírás"
          v-model="quiz.description"
      ></v-textarea>

      <div class="my-2 mb-5">
        <v-btn
            prepend-icon="mdi-content-save-outline"
            color="success"
            class="mr-3"
            type="submit"
        >
          Mentés
        </v-btn>
      </div>

      <v-card
          title="Kérdések"
          class="px-1"
      >

        <v-input
            :rules="questionsRule"
        ></v-input>

        <v-card v-for="(question, index ) in quiz.questions" :key="question.id" variant="outlined"
                class="mt-3 px-4 py-4">

          <v-input
              :rules="questionRule(question)"
          ></v-input>

          <v-text-field
              v-model="question.question"
              label="Kérdés"
              required
              :rules="ValidationRules.requiredRule"
          >
          </v-text-field>
          <div class="grid grid-cols-4 gap-3">
            <div>
              <v-text-field
                  v-model="question.points"
                  label="Pontszám"
                  type="number"
                  required
                  :rules="ValidationRules.positiveNumberRule"
              >
              </v-text-field>
            </div>
            <div class="col-span-3">
              <div v-for="(answer, index) in question.answers" :key="answer.id">

                <div class="flex align-middle">
                  <v-radio
                      class="no-grow mr-8 mb-5 w-7"
                      v-model="answer.accepted"
                      @click="selectAnswer(question.answers,answer)"
                  ></v-radio>
                  <v-text-field
                      v-model="answer.text"
                      label="Válasz"
                      :rules="ValidationRules.requiredRule"
                  >
                  </v-text-field>
                  <v-btn
                      icon="mdi-minus"
                      color="warning"
                      size="x-small"
                      class="ml-3 mt-3"
                      @click="removeAnswer(question.answers,index)"
                  ></v-btn>
                </div>
              </div>

              <div class="flex justify-end">
                <v-btn
                    icon="mdi-plus"
                    color="blue"
                    size="small"
                    @click="addAnswer(question.answers)"
                ></v-btn>

                <v-btn
                    icon="mdi-delete"
                    color="red"
                    size="small"
                    class="ml-5"
                    @click="removeQuestion(index)"
                ></v-btn>
              </div>

            </div>
          </div>
        </v-card>

        <div class="flex justify-center my-5">
          <v-btn
              icon="mdi-plus"
              color="success"
              size="large"
              class="mx-3"
              @click="addQuestion"
          ></v-btn>
        </div>

      </v-card>

    </v-form>
  </div>

</template>

<script>
import {useRoute} from "vue-router";
import {v4 as uuidv4} from 'uuid';
import {ValidationRules} from "@/util/utils";

export default {
  name: "QuizEditorView",
  computed: {
    ValidationRules() {
      return ValidationRules
    },
    questionsRule() {
      return [
        () => this.quiz.questions.length > 0 || "Legalább egy kérdésnek szerepelnie kell a kvízben."
      ]
    }
  },
  data() {
    return {
      id: null,
      quiz: {
        id: null,
        name: null,
        description: null,
        questions: []
      },
      valid: true,
    }
  },
  methods: {
    selectAnswer(answers, answer) {
      answers.forEach(e => e.accepted = false)
      answer.accepted = true
    },
    addAnswer(answers) {
      answers.push(
          {
            id: uuidv4(),
            text: null,
            accepted: false
          },
      )
    },
    removeAnswer(options, index) {
      options.splice(index, 1)
    },
    removeQuestion(index) {
      this.quiz.questions.splice(index, 1)
    },
    addQuestion() {
      if (!this.quiz.questions) {
        this.quiz.questions = []
      }
      this.quiz.questions.push(
          {
            id: uuidv4(),
            question: null,
            answers: [],
            points: 0
          },
      )
    },
    async submit(event) {
      await event
      if (this.valid) {
        this.$rest.saveQuiz(this.quiz, (response) => {
          this.quiz = response.data
          this.$emit("actionSuccess")
          this.$router.push(`/quiz-list`)
        })
      }
    },
    loadData() {
      this.$rest.loadQuiz(this.id, (response) => {
        this.quiz = response.data
      })
    },

    questionRule(question) {
      return [
        () => question.answers.length > 1 || "A kérdésnek legalább két válaszlehetőséget tartalmaznia kell.",
        () => {
          let hasRightAnswer = false
          for (let answer of question.answers) {
            if (answer.accepted) {
              hasRightAnswer = true
            }
          }
          return hasRightAnswer || "Egy választ helyesnek kell jelölni."
        },
      ]
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


.no-grow {
  flex-grow: 0;
}

</style>