import {Component, OnInit, OnChanges, Output, EventEmitter,forwardRef } from '@angular/core';
import {Observable, empty} from 'rxjs';
import {debounceTime, distinctUntilChanged, map, switchMap, catchError} from 'rxjs/operators';
import { GeoportalApiService } from '../../services/geoportal-api.service';
import { ControlValueAccessor,DefaultValueAccessor,NG_VALUE_ACCESSOR   } from '@angular/forms';





@Component({
  selector: 'app-autosuggest',
  templateUrl: './autosuggest.component.html',
  styleUrls: ['./autosuggest.component.css'],
  providers: [
  { 
    provide: NG_VALUE_ACCESSOR,
    multi: true,
    useExisting: forwardRef(() => AutosuggestComponent),  
  }
]
})


export class AutosuggestComponent implements OnInit, ControlValueAccessor, OnChanges {
  @Output() term: EventEmitter<string> = new EventEmitter<string>();
  @Output() selected: EventEmitter<string> = new EventEmitter<string>();
  public _model: any;
  public touched = false;
  constructor(private apiService: GeoportalApiService) {

  }
  onChange = (delta: any) => {};
   
  onTouched = () => {
    this.touched = true;
  };

  public writeValue(delta: any): void {
    this.model = delta;
    this.onChange(delta);
  }

  public registerOnChange(fn: (v: any) => void): void {
    this.onChange = fn;
  }    
  public registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }
  get model() {
    return this._model;
  }

  set model(val) {
    
    if(val == null){
      this._model = "";
      this.onChange("");
      return;
    }
    this._model = val;
    if( typeof val == "string"){
      this.onChange(this._model);
    }else{
      this.onChange(this._model.name);
      this.selected.emit(this._model.name);
    }
  }
  ngOnChanges(){

  }
  ngOnInit() {
  
  }
  search = (text$: Observable<string>)=>{
    let lst=[];

    return text$.pipe(
      debounceTime(50),
      distinctUntilChanged(),

      switchMap( (searchText) =>  {
      
        if(searchText.length > 4){
          return this.apiService.autocomplete(searchText);
        }else{
          return ( new Observable<Array<any>>(obj=>obj.next([])));
        }
      }),
      map(
        (data)=>{
          lst.length = 0;
          let keys = Object.keys(data);
          for(let key of keys){
            lst.push(
              {
                name:key,
                city:data[key]
              }
              );
          }
          return lst;
        }
      ),
      catchError((error)=>{
        return empty();
      })
    );
  }
  formatter(data){
    return data.name;
  }
}
