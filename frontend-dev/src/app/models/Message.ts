export class Message{
  id: number;
	body: string;
	subject: string;
	conversationId: number;
	senderId: number;
	receiverId: number;
	ownerId: number;
	sent: Date;
	read: Date;
	isRead: boolean;
	constructor(data: any){
		this.setData(data);
	}
	
	setData(data: any){
    if(data['id']){
      this.id = data['id'];
    }
		if(data['body']){
			this.body = data['body'];
		}
		if(data['subject']){
			this.subject = data['subject'];
		}
		if(data['conversationId']){
			this.conversationId = data['conversationId'];
		}
		if(data['senderId']){
			this.senderId = data['senderId'];
		}
		if(data['receiverId']){
			this.receiverId = data['receiverId'];
		}
		if(data['ownerId']){
			this.ownerId = data['ownerId'];
		}
		if(data['read']){
			this.read = new Date(data['read']);
		}
		if(data['sent']){
			this.sent = new Date(data['read']);
		}
	}
}