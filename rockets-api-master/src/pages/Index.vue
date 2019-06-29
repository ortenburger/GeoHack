<template>
  <q-page class="flex flex-center">
    <q-modal v-model="showing">
      <q-card>
        <q-card-title>New Analysis Layer</q-card-title>
        <q-card-main>
          <q-list>
            <q-item>
              <q-item-main>
                <q-field label="Name: "><q-input /></q-field>
              </q-item-main>
            </q-item>
            <q-item>
              <q-item-main>
                <q-field label="Description: "><q-input /></q-field>
              </q-item-main>
            </q-item>
            <q-item>
              <q-item-main>
                <q-field label="Type: "><q-select :options="analysisOptions"
                    v-model="analysis" /></q-field>
              </q-item-main>
            </q-item>
            <q-item>
              <q-item-main>
                <q-field label="Computing Environment: "><q-select
                         :options="envOptions" v-model="env" /></q-field>
              </q-item-main>
            </q-item>
          </q-list>
          <q-list>
            <q-list-title>Select Data Layers</q-list-title>
            <q-item v-for="source in sources">
              <q-item-side>
                <q-checkbox v-model="source.selected" />
              </q-item-side>
              <q-item-main> {{ source.title }} </q-item-main>
            </q-item>
          </q-list>
        </q-card-main>
        <q-card-separator />
        <q-card-actions align='center'>
          <q-btn label="Create" outline color='green'/>
          <q-btn label="Cancel" outline color='red'/>
        </q-card-actions>
      </q-card>
    </q-modal>
    <l-map :center="center" :zoom="zoom" style="height:100%; width:100%;position:absolute;">
      <l-tile-layer :url="url" :attribution="attribution" />
      <l-geo-json />
    </l-map>
  </q-page>
</template>

<style>
html, body, #app {
  height: 100%;
  margin: 0;
}
</style>

<script>

export default {
  name: 'PageIndex',

  data() {
    return {
      zoom: 13,
      url:'http://{s}.tile.osm.org/{z}/{x}/{y}.png',
      url2: 'https://cartodb-basemaps-{s}.global.ssl.fastly.net/dark_all/{z}/{x}/{y}.png',
      url3: 'https://cartodb-basemaps-{s}.global.ssl.fastly.net/light_all/{z}/{x}/{y}.png',
      attribution:'&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors',
      center: L.latLng(51.266778, 7.144975),
      showing: true,
      sources: [],
      analysis: 'geojson',
      analysisOptions: [
        { value: 'geojson', label: 'Geospatial' },
        { value: 'regression', label: 'Regression Analysis' }
      ],
      envOptions: [
        { value: 'python', label: 'Python' },
        { value: 'r', label: 'R' },
        { value: 'spss', label: 'SPSS' }
      ],
      env: 'r',
    };
  },

  mounted() {
    this.getSources();
  },

  methods: {
    async getSources() {
      const url = '/api/v1/sources/';
      const result = await this.$axios.get(url);
      this.sources = result.data.map((source) => {
        source.selected = false;
        return source;
      });
    }
  },

  computed: {
    selected() {
      return this.sources.filter(s => s.selected && s.resources.filter(k => k.format === 'geojson').length > 0);
    },
  },
}
</script>
