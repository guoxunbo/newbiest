import  React, { Component } from 'react';
import JsBarcode from 'jsbarcode';
import './SimpleBarCode.scss';
import { Application } from '../../api/Application';

export default class SimpleBarCode extends Component {

    static displayName = 'SimpleBarCode';

    constructor(props) {
        super(props);
        this.state = {
          value: props.value, 
        };
      }
    
    componentDidMount() {
        JsBarcode(this.barcode, this.state.value, Application.jsBarCode);
    }
    
    render() {
      return (
        <div className="barcode-box">
          <svg
            ref={(ref) => {this.barcode = ref;}}
          />
        </div>
        
      );
    }
}