<template>
    <v-app>
        <v-container>
            <v-text-field
                    v-model="datasets_filter"
                    label="Filter für Datasets"
            ></v-text-field>
            <v-switch
                    small
                    v-for="(val,key) in fdata"
                    v-model="fshow[key]" :label="key"
                    v-if="!datasets_filter||datasets_filter===''||(datasets_filter&&key.toLowerCase().includes(datasets_filter.toLowerCase()))"
            />
            <no-ssr>
                <l-map class="mini-map" :zoom="12" :center="center">
                    <l-tile-layer
                            url="https://api.tiles.mapbox.com/v4/mapbox.streets/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw"
                    ></l-tile-layer>


                    <!--MARKERS FOR FILTERS-->
                    <div v-for="(filter, fkey) in fdata" :key="fkey"
                         v-if="fshow[fkey]"
                    >
                        <div v-for="(val,key) in filter.features" :key="key">
                            <!--Markers for the Point-->
                            <div v-if="val.geometry.type==='Point'">
                                <l-marker :lat-lng="[
                                val.geometry.coordinates[1],
                                val.geometry.coordinates[0]
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
                            <!--Polygons for Lines and Polygons-->
                            <div v-if="val.geometry.type!='Point'">
                                <l-geo-json
                                        :geojson="val"
                                        :options-style="styleFunction"
                                        :options="options"
                                >

                                </l-geo-json>
                            </div>

                        </div>

                    </div>
                    <!--END MARKERS FOR FILTERS-->
                </l-map>
            </no-ssr>
        </v-container>
    </v-app>
</template>

<script>
  import axios from "axios";

  import {mundraub} from "./mundraub.js";
  import {carsharing} from "./Carsharing_EPSG4326_KML.js";
  import {charg_station_cars} from "./Ladestationen-E-Autos_EPSG4326_KML.js";
  import {charg_station_bikes} from "./Ladestationen-E-Fahrraeder_EPSG4326_KML.js";
  import {Radabstellanlagen} from "./Radabstellanlagen_EPSG4326_KML.js";
  import {Schwebebahnhoefe} from "./Schwebebahnhoefe_EPSG4326_JSON.js";
  import {Verleih_E_Fahrraeder} from "./Verleih-E-Fahrraeder_EPSG4326_KML.js";
  import {Zugaenge_Bahntrassenradwege} from "./Zugaenge-Bahntrassenradwege-Punkte_EPSG4326_KML.js";


  export default {

    data: () => ({
      // Travel time describes the stage on which it is traveling
      datasets_filter: null,
      fdata: {
        mundraub: mundraub,
        carsharing: carsharing,
        charg_station_cars: charg_station_cars,
        charg_station_bikes: charg_station_bikes,
        Radabstellanlagen: Radabstellanlagen,
        Schwebebahnhoefe: Schwebebahnhoefe,
        Verleih_E_Fahrraeder: Verleih_E_Fahrraeder,
        Zugaenge_Bahntrassenradwege: Zugaenge_Bahntrassenradwege,
        Naturschutzgebiete: null,
        Radrouten: null,
        Stadtbezirke: null,
        // Tempo30:null
      },
      fshow: {
        mundraub: false,
        carsharing: false,
        charg_station_cars: false,
        charg_station_bikes: false,
        Radabstellanlagen: false,
        Schwebebahnhoefe: false,
        Verleih_E_Fahrraeder: false,
        Zugaenge_Bahntrassenradwege: false,
        Naturschutzgebiete: false,
        Radrouten: false,
        Stadtbezirke: false,
        // Tempo30: false,
      },
      center: [51.2917076298, 7.2510191991],
      loading: false,
    }),
    methods: {
      calc: function () {
        axios
            .get("Radrouten_EPSG4326_KML.json")
            .then(response => {
              console.log("Loading:");
              this.fdata.Radrouten = response.data;

            })
            .catch(error => {
              // Handle Errors here.
              var errorCode = error.code;
              var errorMessage = error.message;
              console.log(errorCode, errorMessage);
              // ...
            });
        axios
            .get("Naturschutzgebiete_EPSG4326_JSON.json")
            .then(response => {
              console.log("Loading:");
              this.fdata.Naturschutzgebiete = response.data;

            })
            .catch(error => {
              // Handle Errors here.
              var errorCode = error.code;
              var errorMessage = error.message;
              console.log(errorCode, errorMessage);
              // ...
            });
        axios
            .get("Stadtbezirke_EPSG4326_JSON.json")
            .then(response => {
              console.log("Loading:");
              this.fdata.Stadtbezirke = response.data;

            })
            .catch(error => {
              // Handle Errors here.
              var errorCode = error.code;
              var errorMessage = error.message;
              console.log(errorCode, errorMessage);
              // ...
            });
        // axios
        //     .get("Tempo30-Zonen_EPSG4326_KML.json")
        //     .then(response => {
        //       console.log("Loading:");
        //
        //       this.fdata.Tempo30 = response.data;
        //       console.log(this.fdata.Tempo30);
        //
        //     })
        //     .catch(error => {
        //       // Handle Errors here.
        //       var errorCode = error.code;
        //       var errorMessage = error.message;
        //       console.log(errorCode, errorMessage);
        //       // ...
        //     });

      }
    },
    watch: {},
    created() {

    },
    mounted() {
      this.calc();
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
