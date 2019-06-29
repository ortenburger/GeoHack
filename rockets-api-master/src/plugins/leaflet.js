import Vue2Leaflet from 'vue2-leaflet'
import L from 'leaflet'

// require('leaflet.tilelayer.pouchdbcached')
require('../../node_modules/leaflet/dist/leaflet.css')

var { LMap, LTileLayer, LGeoJSON } = Vue2Leaflet;
// leave the export, even if you don't use it
export default ({ Vue }) => {

  // Register Vue2Leaflet components
  Vue.component('l-map', LMap)
  Vue.component('l-tile-layer', LTileLayer)
  Vue.component('l-geo-json', LGeoJSON)
}
