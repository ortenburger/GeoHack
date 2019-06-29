import { Role } from './Role';
export class User {
    id: number;
    username: string;
    password: string;
    firstname: string;
    lastname: string;
    email: string;
    roles: Role[];
    accountCreated: string;
    eula_version: string;
    description: string;
    isAdmin: boolean = false;
    isRedakteur: boolean = false;
    
    
  clear():void{
      this.id = -1;
      this.username ="";
      this.password ="";
      this.firstname ="";
      this.lastname ="";
      this.email = "";
      this.roles.length = 0;
      this.accountCreated = "";
      this.eula_version = "";
      this.description = "";
      this.isAdmin = false;
      this.isRedakteur = false;
  }
    constructor(data:any){
       this.setData(data);
      
    }
  hasRole(search: String): boolean{
    for(const role of this.roles){
      if(role.name === search){
        return true;
      }
    }
    return false;
  }
  
  
  setData(data:any){
      
      if(data['id']){
        this.id = data['id'];
      }
      if(data['username']){
        this.username = data['username'];
      }
      if(data['firstname']){
        this.firstname = data['firstname'];
      }
      if(data['lastname']){
        this.lastname = data['lastname'];
      }
      if(data['eula_version']){
        this.eula_version = data['eula_version'];
      }
      if(data['email']){
        this.email = data['email'];
      }
      if(data['description']){
        this.description = data['description'];
      }
      if(!this.roles){
        this.roles=new Array<Role>();
      }
      if(data['roles']){
        data['roles'].forEach(
          (element)=>{
            this.roles.push( new Role( element ) );
          });
      }
      this.isAdmin = this.hasRole('admin');
      this.isRedakteur = this.hasRole('redakteur');
    }
      
    toString(): string {
      
      let msg: string = "Username: " + this.username;
      
      if(this.roles){
        msg += "\nRoles:\n";
        for(const role of this.roles){
          msg += "["+role.name+"]\n";
        }
      }else{
        msg += "\nRoles: Empty";
      }
      return msg;
    }
}
