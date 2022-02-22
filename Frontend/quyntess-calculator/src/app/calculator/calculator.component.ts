import {Component, OnInit} from '@angular/core';
import {CalcServiceService} from "./service/calc-service.service";
import {ResponseModel} from "./model/response.model";

@Component({
  selector: 'app-calculator',
  templateUrl: './calculator.component.html',
  styleUrls: ['./calculator.component.scss']
})
export class CalculatorComponent implements OnInit {

  expr1 = '';
  expr2 = '';
  closeResult = '';
  resp: ResponseModel = {result: []};
  errorMessage = '';

  constructor(private calcService: CalcServiceService) {
  }

  ngOnInit(): void {
  }

  resetExpr() {
    this.expr1 = '';
    this.expr2 = '';
  }

  calcExprs() {
    this.errorMessage = '';
    this.resp = {result: []};
    const body = {'expr': [this.expr1, this.expr2]};
    this.calcService.calcExprs(body).subscribe(resp => {
        const r = resp as ResponseModel;
        if (r && r.result && r.result.length > 0) {
          this.resp = r;
          this.resp.result.forEach((s, index = 0) => {
            if (s === 'undefined') {
              this.resp.result[index] = '';
            }
          });
        }
      },
      errorResp => {
        this.errorMessage = errorResp.error.error;
      }
    );
  }
}
