<template>
  <router-link :to="route">
    <v-list-item
        :prepend-icon="icon"
        :value="route"
        class="border-b"
    >
      <v-list-item-title
          class="menu-text"
      >
        <div class="flex align-center justify-between mt-1 mb-1">
          <div>
            {{ title }}
          </div>
          <div class="text-sm flex justify-end w-full" v-if="showCount && count">
            <div class="bg-red-700 rounded text-white pl-1 pr-1">{{ count }}</div>
          </div>

        </div>
      </v-list-item-title>
    </v-list-item>
  </router-link>
</template>

<script>
export default {
  name: "MenuItem",
  props: {
    title: String,
    route: String,
    icon: String,
    count: Number,
    showCount: Boolean,
  },
}
</script>

<style scoped>
.menu-text {
  font-size: 1rem !important;
}
</style>