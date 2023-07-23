<template>
  <div class="bg-gray-300 my-4">
    <div class="grid grid-cols-4">
      <div class="col-span-3">
        <h1 class="text-lg">{{ clazz.name }}</h1>
        <p>{{ clazz.description }}</p>
      </div>
      <div class="grid grid-cols-2 py-2">
        <div>
          <button class="btn bg-green-500" @click="routeToDetail(clazz.id)">Szerkeszt</button>
        </div>
        <div>
          <button class="btn bg-red-500">Torol</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "ClassTile",
  props: ['clazz'],
  methods: {
    routeToDetail(id) {
      this.$router.push(`/teacher-class-detail/${id}`)
    }
  },
}
</script>

<style scoped>
.btn {
  @apply text-white font-bold py-2 px-4 rounded
}

</style>