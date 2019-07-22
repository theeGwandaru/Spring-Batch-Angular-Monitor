import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class FileJobService {

  constructor(private httpClient: HttpClient) { }

  public getFileJobs(): Observable<any>{
    const httpOptions = {
      'Content-Type': 'application/json'
    }
    return this.httpClient.get(environment.apiUrl + 'file-jobs')
  }

  public startFileJob(fileJob: any):Observable<any>{
    const httpOptions = {
      'Content-Type': 'application/json'
    }
    return this.httpClient.post(environment.apiUrl + 'file-jobs/start', fileJob);
  }
}
