export class Role {

  name: string;
  id: number;

  setData( data: any ) {
    if ( data[ 'id' ] ) {
      this.id = data['id'];
    }
    if ( data[ 'name' ] ) {
      this.name = data[ 'name' ];
    }
    if ( data[ 'permissions' ] ) {
      // TODO
    }
  }


  constructor( data: any ) {
    this.setData(data);
  }
}
