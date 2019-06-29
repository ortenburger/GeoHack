<template>
    <v-app>
        <v-container>
            <v-switch
                    v-for="(val,key) in fdata"
                    v-model="fshow[key]" :label="key"/>
            <no-ssr>
                <l-map class="mini-map" :zoom="12" :center="center">
                    <l-tile-layer
                            url="https://api.tiles.mapbox.com/v4/mapbox.streets/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw"
                    ></l-tile-layer>


                    <!--MARKERS FOR FILTERS-->
                    <div v-for="(filter, fkey) in fdata" :key="fkey"
                         v-if="fshow[fkey]"
                    >
                        <l-marker
                                v-for="(val,key) in filter.features"
                                :lat-lng="[
                                val.geometry.coordinates[0],
                                val.geometry.coordinates[1]
                                ]"
                        >
                            <l-icon :icon-anchor=" [30/2, 0]">
                                <v-img height="30" width="30"
                                       contain
                                       src="https://mundraub.org/modules/custom/mundraub_map/img/campaigns.svg"/>
                            </l-icon>
                            <l-popup>
                                <v-layout column style="text-align: center">

                                    <v-flex v-for="(val1,key1) in val.properties">
                                        {{key1}}:{{val1}}
                                    </v-flex>
                                </v-layout>

                            </l-popup>

                        </l-marker>
                    </div>
                    <!--END MARKERS FOR FILTERS-->
                </l-map>
            </no-ssr>
        </v-container>
    </v-app>
</template>

<script>
  import {mundraub} from "./mundraub.js";
  export default {

    data: () => ({
      // Travel time describes the stage on which it is traveling
      fdata: {
        mundraub: mundraub,
      },
      fshow: {
        mundraub:false,
      },
      center: [51.2917076298, 7.2510191991],
      loading: false,
    }),
    methods: {
    },
    watch: {},
    created() {
    },
    computed: {

      options() {
        return {
          onEachFeature: this.onEachFeatureFunction
        };
      },
      styleFunction() {
        return () => {
          return {
            weight: 2,
            color: "#9ca8f1",
            opacity: 1,
            fillColor: "#7b83e4",
            fillOpacity: 0.02
          };
        };
      },
      onEachFeatureFunction() {
        return (feature, layer) => {
          let tool_tip = "";
          for (let key in feature.properties) {
            tool_tip = tool_tip + "<div>" + key + ":" + feature.properties[key] + " </div>"
          }
          layer.bindTooltip(
              tool_tip,
              {permanent: false, sticky: true}
          );
        };
      },
    },
  }
  ;
</script>

<style scoped>
    .mini-map {
        width: 100%;
        height: 50vh !important;
    }
</style>
