//import { Component } from '@angular/core';
//
//@Component({
//  selector: 'app-root',
//  templateUrl: './app.component.html',
//  styleUrls: ['./app.component.css']
//})
//export class AppComponent {
//  title = 'sales-app';
//}
//
import { Component, OnInit } from '@angular/core';
import { ClientService } from './client.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  clients: any[];

  constructor(private clientService: ClientService) { }

  ngOnInit() {
    this.clientService.getClientsList().subscribe(data => {
      this.clients = data;
    });
  }

  addClient() {
    const client = { name: 'New Client' };
    this.clientService.createClient(client)
      .subscribe(data => {
        this.clients.push(data);
      });
  }

  updateClient(client) {
    this.clientService.updateClient

