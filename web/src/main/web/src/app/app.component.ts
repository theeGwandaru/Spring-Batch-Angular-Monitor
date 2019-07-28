import {Component, OnInit} from '@angular/core';
import {FileJobService} from "./_services/file-job.service";
import {Subscription} from "rxjs";
import { Message } from '@stomp/stompjs';
import {RxStompService} from "@stomp/ng2-stompjs";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
    title = 'Spring-Batch-Angular-Monitor';
    public availableFileJobs = [];
    public receivedMessages: string[] = [];
    private topicSubscription: Subscription;

    constructor(private fileJobService: FileJobService, private rxStompService: RxStompService) {

    }

    ngOnInit() {
        this.fileJobService.getFileJobs()
            .subscribe((response) => {
                this.availableFileJobs = response;
            });

        this.topicSubscription = this.rxStompService.watch('/topic/public').subscribe((message: Message) => {
            console.log(message.body);
            let jsonMessage = JSON.parse(message.body)
            this.availableFileJobs.forEach((availableFileJob)=>{
                if(availableFileJob.absolutePath == jsonMessage.fileName){
                    availableFileJob.processedPercentage = jsonMessage.percentageComplete;
                }
            });
            this.receivedMessages.push(message.body);
        });
    }

    ngOnDestroy() {
        this.topicSubscription.unsubscribe();
    }

    startJob(fileJob) {
        this.availableFileJobs.forEach((availableFileJob)=>{
            if(availableFileJob.absolutePath == fileJob.fileName){
                availableFileJob.processedPercentage = 0;
            }
        });
        this.fileJobService.startFileJob(fileJob)
            .subscribe((response) => {
                console.log(response);
            })
    }

}
