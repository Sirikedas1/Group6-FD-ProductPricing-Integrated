import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

/** Dashboard / landing page with a card for each module. */
@Component({
  selector: 'app-home',
  imports: [RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  private readonly route = inject(ActivatedRoute);

  /** True when the user was sent here because their role may not open a page. */
  readonly denied = this.route.snapshot.queryParamMap.has('denied');
}
