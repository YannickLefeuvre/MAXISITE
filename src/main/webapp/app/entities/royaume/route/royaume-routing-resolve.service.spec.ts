import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IRoyaume, Royaume } from '../royaume.model';
import { RoyaumeService } from '../service/royaume.service';

import { RoyaumeRoutingResolveService } from './royaume-routing-resolve.service';

describe('Royaume routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: RoyaumeRoutingResolveService;
  let service: RoyaumeService;
  let resultRoyaume: IRoyaume | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(RoyaumeRoutingResolveService);
    service = TestBed.inject(RoyaumeService);
    resultRoyaume = undefined;
  });

  describe('resolve', () => {
    it('should return IRoyaume returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultRoyaume = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultRoyaume).toEqual({ id: 123 });
    });

    it('should return new IRoyaume if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultRoyaume = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultRoyaume).toEqual(new Royaume());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Royaume })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultRoyaume = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultRoyaume).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
