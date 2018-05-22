class AbstractConfirm {

    constructor(title, text){
        this.title = title;
        this.text = text;

    }   
    okAdaptor() {
        alert("OK");
    }

    buildConfirmStack() {
        return {
            'dir1': 'down', 
            'modal': true, 
            'firstpos1': 25
        };
    }
}

export {AbstractConfirm}