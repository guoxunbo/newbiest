import EntityForm from "./EntityForm";
import UserManagerRequest from "../../api/user-manager/UserManagerRequest";

export default class UserForm extends EntityForm {

    handleSave = () => {
        let self = this;
        let object = {
            user: this.props.object,
            success: function(responseBody) {
                if (self.props.onOk) {
                    self.props.onOk(responseBody.user);
                }
            }
        }
        UserManagerRequest.sendMergeUserRequest(object);
    }
}