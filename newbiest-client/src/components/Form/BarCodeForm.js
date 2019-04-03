import  React, { Component } from 'react';

import EntityForm from './EntityForm';
import SimpleBarCode from '../code/SimpleBarCode';

/**
 * 展示一维码以及二维码的Form
 */

export default class BarCodeForm extends EntityForm {

    static displayName = 'BarCodeForm';

    constructor(props) {
        super(props);
    }

    handleSave= () => {
        this.props.onOk();
    }

    buildForm = () => {
        return (<SimpleBarCode value={this.props.value}></SimpleBarCode>)
    }


}