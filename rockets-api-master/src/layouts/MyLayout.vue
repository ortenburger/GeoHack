<template>
  <q-layout view="lHh Lpr lFf">
    <q-layout-header>
      <q-toolbar
        color="secondary"
      >
        <q-toolbar-title>
          GeoPortal Mockup
          <div slot="subtitle">Just as an example...</div>
        </q-toolbar-title>
      </q-toolbar>
    </q-layout-header>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script>
import { openURL } from 'quasar'

export default {
  name: 'MyLayout',
  data () {
    return {
    }
  },
  methods: {
    openURL
  }
}
</script>

<style>
</style>
