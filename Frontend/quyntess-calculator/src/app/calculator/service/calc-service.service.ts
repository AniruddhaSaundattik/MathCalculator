import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class CalcServiceService {

  constructor(private httpClient: HttpClient) {
  }

  calcExprs(body: any) {
    const url = 'http://localhost:8080/quyntess-calculator/calc';
    const response = this.httpClient.post(url, JSON.stringify(body),
      {headers: {'Access-Control-Allow-Origin': 'http://localhost:8080'}});
    return response;
  }
}
