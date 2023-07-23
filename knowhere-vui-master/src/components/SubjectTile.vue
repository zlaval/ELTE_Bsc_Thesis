<template>

  <v-card variant="outlined"
          @click="routeToDetail(subject.id)"
          class="subject-tile flex "
  >
    <v-card-item class="w-full">
      <div>
        <img class="subject-tile-background" v-if="subject.coverImagePath" :src="getImageSource()">
        <div class="subject-tile-background" v-if="!subject.coverImagePath"></div>
      </div>
      <div class="subject-title">
        {{ subject.name }}
      </div>
      <div class="v-card-subtitle">
        {{ getFormattedDate() }}
      </div>
      <div class="text-caption mt-2 h-16">
        {{ getCroppedText(subject.description) }}
      </div>


      <div class="mt-5 mb-3 mx-3 flex justify-between">
        <div>
          <v-chip prepend-icon="mdi-credit-card-edit-outline">
            {{ subject.credit }}
          </v-chip>
        </div>
        <div>
          <v-chip prepend-icon="mdi-account-group-outline" v-if="subject.occupiedSeats!==null">
            {{ subject.occupiedSeats }}/ {{ subject.seats }}
          </v-chip>
          <v-chip prepend-icon="mdi-account-group-outline" v-if="subject.occupiedSeats===null">
            nincs adat / {{ subject.seats }}
          </v-chip>
        </div>
      </div>
    </v-card-item>
  </v-card>
</template>

<script>
import axios from "axios";
import {formatDateHu, getCroppedText} from "@/util/utils";

export default {
  name: "SubjectTile",
  props: ['subject'],
  data() {
    return {
      rating: 0
    }
  },
  methods: {
    getCroppedText,
    getImageSource() {
      return `${axios.defaults.baseURL}/subject/resource/image/${this.subject.coverImagePath}`
    },
    getFormattedDate() {
      const start = formatDateHu(this.subject.startDt)
      let end = 'Korlátlan'
      if (this.subject.endDt) {
        end = formatDateHu(this.subject.endDt)
      }
      return `${start} - ${end}`
    },
    routeToDetail(id) {
      this.$router.push(`/teacher-subject-detail/${id}`)
    }
  },
}
</script>

<style scoped>

.subject-title {
  @apply text-blue-600 font-bold text-xl mt-3 mb-1
}

.subject-tile {

  @apply p-1 min-h-[30vh]
}

.subject-tile-background {
  width: 100%;
  height: 10vh;
  object-fit: cover;
}


</style>