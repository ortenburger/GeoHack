import { Injectable } from '@angular/core';
import { OsmNode } from '../models/OsmNode';
import { OsmNodeFactory } from '../models/OsmNodeFactory';
import { Rectangle } from '../models/Rectangle';
import { GeoportalApiService } from './geoportal-api.service';
import { CacheService } from './cache.service';
import { LogService } from './log.service';
import { OverpassService } from './overpass.service';
import { BliService } from './bli.service';

@Injectable({
    providedIn: 'root'
})
export class GeoElementService {

    nodes: Array<OsmNode>;
    constructor(
        private logger: LogService,
        private apiService: GeoportalApiService,
        private cache: CacheService
    ) {
        this.nodes = new Array<OsmNode>();
    }
    
    elementSaved(response) {
        this.logger.debug('GeoElementService: successfully saved item. response: ' + JSON.stringify(response));
        this.logger.notify('success', 'update successful.');
    }
    
    elementFailedToSave(errorResponse) {
        this.logger.debug("GeoElementService: Error saving element.");
        if (errorResponse.error && errorResponse.error.error) {
            this.logger.notify('error', 'error saving element: ' + errorResponse.error.error);
        } else {
            this.logger.notify('error', 'unknown error while saving element');
        }
        this.logger.debug(JSON.stringify(errorResponse));
    }
    
    save(element: OsmNode) {
        this.logger.debug("GeoElementService: sending node to server (save).");
        this.logger.debug(JSON.stringify(element));
        return this.apiService.saveGeoElement(element).subscribe(
            (response) => {
                this.logger.debug("GeoElementService: got response from server after create-operation.");
                this.elementSaved(response);
            },
            (error) => {
                this.logger.debug("GeoElementService: got error back: " + JSON.stringify(error, null, 4));
                this.elementFailedToSave(error);
            });
    }

    update(element: OsmNode) {
        this.logger.debug("GeoElementService: updating node on server (update).");
        this.logger.debug("Element: "+element.debugString());
        return this.apiService.updateGeoElement(element).subscribe(
            (response) => {
                this.logger.debug("GeoElementService: got response from server after update-operation.");
                this.elementSaved(response);
            },
            (error) => {
                this.elementFailedToSave(error);
            });
    }

    getGeoItemById(id: number) {
        this.apiService.getGeoElementById(id).subscribe(
            (response) => {
                this.parseResponse(response);
            },
            (error) => {
                this.parseError(error);
            }
        );
    }

    parseResponse(response: any) {
        this.logger.debug("GeoElementService: Got response.");
    }

    parseError(error: any) {
        this.logger.debug("GeoItemService: Got error-response.");
    }
}
