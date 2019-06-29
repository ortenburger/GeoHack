<template>
    <v-app>
        <v-container>
            <v-switch v-model="show" :label="`Anzeigen: ${show.toString()}`"></v-switch>

            <v-btn @click="get_data" :disabled="loading">
                get_line
            </v-btn>
            <v-btn @click="get_coords" :disabled="loading">
                get_coords
            </v-btn>
            <v-text-field
                    v-model="address"
                    label="Enter the destination address here"
            ></v-text-field>

            <v-layout row>
                <v-flex>
                    <v-text-field
                            v-model="origin"
                            label="Origin"
                    ></v-text-field>
                </v-flex>
                <v-flex>
                    <v-text-field
                            v-model="dest"
                            label="Destination"
                    ></v-text-field>
                </v-flex>
                <v-flex>
                    <v-select
                            value="Transport"
                            v-model="trasport"
                            :items="['bike','foot','car']"
                            menu-props="auto, overflowY"

                    ></v-select>
                </v-flex>
            </v-layout>


            <no-ssr>
                <l-map class="mini-map" :zoom="12" :center="center">
                    <l-tile-layer
                            url="https://api.tiles.mapbox.com/v4/mapbox.streets/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw"
                    ></l-tile-layer>

                    <div v-if="show">
                        <l-geo-json
                                :geojson="stadteile"
                                :options="options"
                                :options-style="styleFunction"
                                v-for="(val1, key) in stadteile" :key="key"
                        >

                        </l-geo-json>

                    </div>
                    <l-geo-json
                            :geojson="geojsons"
                            :options-style="styleFunction"

                    >

                    </l-geo-json>

                </l-map>
            </no-ssr>
        </v-container>
    </v-app>
</template>

<script>
  import axios from "axios";
  import Vue from "vue";
  import {stadteile} from "./Stadtbezirke_EPSG4326_JSON.js";

  var app_key = "e341f9a9-da78-4cfd-bb37" + "-" + "3c682e921182";
  // import {icon} from "leaflet";
  axios.defaults.headers.get = {
    // "Access-Control-Allow-Origin": "*",
    // "Access-Control-Allow-Credentials": "true",
    "Content-Type": "application/json"
  };
  axios.defaults.method = "get";

  export default {

    data: () => ({

      // Travel time describes the stage on which it is traveling
      address: null,
      center: [51.2917076298, 7.2510191991],
      origin: "51.217469, 6.804767",
      dest: "51.2191094, 6.8043112",
      show: false,
      geojson: null,
      stadteile: null,

      trasport: "car",
      markers: [[], [], []],

      loading: false,
      geojsons: [{type: "Point", coordinates: [7.2510191991, 51.2917076298]}]
    }),
    methods: {
      //Calculating route with Graphhopper
      get_data: function () {
        this.loading = true;
        this.geojsons = [];

        var url_1 = "https://graphhopper.com/api/1//route?point=" + this.origin + "&point=" + this.dest + "&type=geojson&locale=de-DE&vehicle=" + this.trasport + "&weighting=fastest&elevation=true&key=" + app_key + "&instructions=false&points_encoded=false";
        axios
            .get(url_1)
            .then(response => {
              console.log("Loading");
              this.geojsons = response.data.paths[0].points;
              console.log(this.geojsons);
              this.loading = false;
              this.center = [this.geojsons.coordinates[0][1], this.geojsons.coordinates[0][0]];
            })
            .catch(error => {
              // Handle Errors here.
              this.loading = false;

              var errorCode = error.code;
              var errorMessage = error.message;
              console.log(errorCode, errorMessage);
              // ...
            });
      },
      //This get work with HERE API
      get_coords: function () {
        this.loading = true;
        console.log("Starting Axsios get_coords");
        let url_1 = "https://geocoder.api.here.com/6.2/geocode.json?searchtext=" + this.address + "&app_id=jmkpDC63yWSbK3" + "644Zdi&app_code=lBPRtQcZIyXg" + "WurwgzCUkw&gen=8";
        axios
            .get(url_1)
            .then(response => {
              console.log("Loading");
              let dest_temp = response.data.Response.View[0].Result[0].Location.NavigationPosition[0];
              this.dest = dest_temp.Latitude + "," + dest_temp.Longitude;
              console.log("Dest", this.dest);
              this.loading = false;
            })
            .catch(error => {
              // Handle Errors here.
              this.loading = false;

              var errorCode = error.code;
              var errorMessage = error.message;
              console.log(errorCode, errorMessage);
              // ...
            });
      }
    },
    watch: {},
    created() {
      this.stadteile = stadteile[0].features;

    },
    computed: {
      api_host() {
        console.log("Get api_host");
        var url_1 = "https://graphhopper.com/api/1//route?point=" + this.origin + "&point=" + this.dest + "&type=geojson&locale=de-DE&vehicle=foot&weighting=fastest&elevation=true&key=" + app_key + "&instructions=false&points_encoded=false";
        return url_1
      },
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
