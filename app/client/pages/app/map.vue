<template>
    <v-app>
        <v-container>
            <v-layout column>
                <!--<v-switch v-model="show" :label="`Anzeigen: ${show.toString()}`"></v-switch>-->
                <v-layout row>
                    <v-btn small @click="get_data" :disabled="loading">
                        Neue Route Berechnen
                    </v-btn>
                    <v-btn small @click="get_coords" :disabled="loading">
                        Zielcoordinaten
                    </v-btn>
                </v-layout>


                <v-layout row wrap>
                    <v-text-field
                            v-model="address"
                            label="Zieladresse"
                    ></v-text-field>
                    <v-flex v-if="false">
                        <v-text-field
                                v-model="origin"
                                label="Origin"
                        ></v-text-field>
                    </v-flex>
                    <v-flex shrink>
                        <v-text-field
                                v-model="dest"
                                label="Ziel Coordinaten"
                        ></v-text-field>
                    </v-flex>
                    <v-flex shrink>
                        <v-select
                                value="Transport"
                                v-model="trasport"
                                :items="['bike','foot','car']"
                                menu-props="auto, overflowY"
                        ></v-select>
                    </v-flex>
                </v-layout>
            </v-layout>


            <no-ssr>
                <l-map class="mini-map" :zoom="14" :center="center">
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
                    <l-marker :lat-lng="[
                    routes.features[0].geometry.coordinates[0][1],
                    routes.features[0].geometry.coordinates[0][0],
                    ]">
                        <l-icon
                                :icon-anchor=" [30/2, 30/2]"
                        >
                            <div style="font-size: 18px">🏡️</div>

                            <!--<v-img height="30" width="30" :src="source[toggle_exclusive].logo"/>-->
                        </l-icon>
                    </l-marker>
                    <l-marker :lat-lng="[
                    routes.features[0].geometry.coordinates[routes.features[0].geometry.coordinates.length-1][1],
                    routes.features[0].geometry.coordinates[routes.features[0].geometry.coordinates.length-1][0],
                    ]">
                        <l-icon
                                :icon-anchor=" [30/2, 30/2]"
                        >
                            <div style="font-size: 18px">🏊‍♀️</div>

                            <!--<v-img height="30" width="30" :src="source[toggle_exclusive].logo"/>-->
                        </l-icon>
                    </l-marker>
                    <l-geo-json
                            :geojson="routes.features[transport_idx]"
                            :options-style="styleFunction"
                    >

                    </l-geo-json>

                </l-map>
            </no-ssr>
            <v-layout row>
                <v-layout column>
                    <v-flex>
                        <v-btn @click="transport_idx=2">
                            🚴‍
                        </v-btn>
                    </v-flex>
                    <v-flex>
                        zeit
                    </v-flex>
                    <v-flex>

                        0.07
                        0.12 / 0.25

                    </v-flex>
                </v-layout>
                <v-layout column>
                    <v-flex>
                        <v-btn @click="transport_idx=1">
                            🚎
                        </v-btn>
                    </v-flex>
                    <v-flex>
                        zeit
                    </v-flex>
                    <v-flex>
                        co2
                    </v-flex>
                </v-layout>
                <v-layout column>
                    <v-flex>
                        <v-btn @click="transport_idx=0">

                            🚗
                        </v-btn>
                    </v-flex>
                    <v-flex>
                        zeit
                    </v-flex>
                    <v-flex>
                        co2
                    </v-flex>
                </v-layout>
                <v-layout column>
                    <v-flex>
                        <v-btn @click="transport_idx=2">
                            🚶‍♀️
                        </v-btn>
                    </v-flex>
                    <v-flex>
                        zeit
                    </v-flex>
                    <v-flex>
                        co2
                    </v-flex>
                </v-layout>
                <v-layout column>
                    <v-flex>
                        <v-btn @click="transport_idx=2">
                            🛵
                        </v-btn>
                    </v-flex>
                    <v-flex>
                        zeit
                    </v-flex>
                    <v-flex>
                        co2
                    </v-flex>
                </v-layout>
                <v-layout column>
                    <v-flex>
                        <v-btn @click="transport_idx=2">
                            🚴‍🔄
                        </v-btn>
                    </v-flex>
                    <v-flex>
                        zeit
                    </v-flex>
                    <v-flex>
                        co2
                    </v-flex>
                </v-layout>

            </v-layout>
        </v-container>
    </v-app>
</template>

<script>
  import axios from "axios";
  import Vue from "vue";
  import {stadteile} from "./Stadtbezirke_EPSG4326_JSON.js";
  import {routes} from "./routes.js";

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
      routes: routes,
      // center: [51.2917076298, 7.2510191991],
      origin: "51.217469, 6.804767",
      dest: "51.2191094, 6.8043112",
      show: false,
      geojson: null,
      stadteile: null,
      transport_idx: 0,

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

        var url_1 = "https://graphhopper.com/api/1//route?point=" +
            routes.features[0].geometry.coordinates[0][1] + "," +
            routes.features[0].geometry.coordinates[0][0] +
            "&point=" + this.dest + "&type=geojson&locale=de-DE&vehicle=" + this.trasport + "&weighting=fastest&elevation=true&key=" + app_key + "&instructions=false&points_encoded=false";
        axios
            .get(url_1)
            .then(response => {
              console.log("Loading");
              this.geojsons = response.data.paths[0].points;
              console.log(this.geojsons);
              this.loading = false;
              // this.center = [this.geojsons.coordinates[0][1], this.geojsons.coordinates[0][0]];
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
      center() {
        // routes.features[this.transport_idx].geometry.coordinates[0]
        let coords = [
          routes.features[0].geometry.coordinates[0][1],
          routes.features[0].geometry.coordinates[0][0]
        ];
        return coords //[51.2917076298, 7.2510191991]
      },

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
            color: "#f11843",
            opacity: 1,
            fillColor: "#e48591",
            fillOpacity: 0.05
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
