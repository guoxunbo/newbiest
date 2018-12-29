export default class Role {
    objectRrn;
    name;
    description;
    users;
    authorities;

    setObjectRrn(objectRrn) {
        this.objectRrn = objectRrn;
    }
    
    setUsers(users) {
        this.users = users;
    }

    setAuthorities(authorities) {
        this.authorities = authorities;
    }
}