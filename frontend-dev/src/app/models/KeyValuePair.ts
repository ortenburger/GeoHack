export class KeyValuePair{
	public id:number;
	public key:string;
	public value:string;
	public source:string;
	public displayName:string;
	constructor(data){
		if(data['id']){
			this.id = data['id'];
		}
		if(data['key']){
			this.key = data['key'];
		}
		if(data['value']){
			this.value = data['value'];
		}
		if(data['displayName']){
			this.displayName = data['displayName'];
		}
		if(data['source']){
			this.source = data['source'];
		}

	}
}