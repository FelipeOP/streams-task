package com.epam.rd.autocode.assessment.basics.collections;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.epam.rd.autocode.assessment.basics.entity.BodyType;
import com.epam.rd.autocode.assessment.basics.entity.Client;
import com.epam.rd.autocode.assessment.basics.entity.Order;
import com.epam.rd.autocode.assessment.basics.entity.Vehicle;

public class Agency implements Find, Sort {

	private List<Vehicle> vehicles;

	private List<Order> orders;

	public Agency() {
		this.vehicles = new ArrayList<>();
		this.orders = new ArrayList<>();
	}

	public void addVehicle(Vehicle vehicle) {
		vehicles.add(vehicle);
	}

	public void addOrder(Order order) {
		orders.add(order);
	}

	@Override
	public List<Vehicle> sortByID() {
		return List.copyOf(vehicles)
				.stream()
				.sorted((v1, v2) -> Long.compare(v1.getId(), v2.getId()))
				.toList();
	}

	@Override
	public List<Vehicle> sortByYearOfProduction() {
		return List.copyOf(vehicles)
				.stream()
				.sorted((v1, v2) -> Integer.compare(v1.getYearOfProduction(), v2.getYearOfProduction()))
				.toList();
	}

	@Override
	public List<Vehicle> sortByOdometer() {
		return List.copyOf(vehicles)
				.stream()
				.sorted((v1, v2) -> Long.compare(v1.getOdometer(), v2.getOdometer()))
				.toList();
	}

	@Override
	public Set<String> findMakers() {
		return vehicles.stream()
				.map(Vehicle::getMake)
				.collect(Collectors.toSet());
	}

	@Override
	public Set<BodyType> findBodytypes() {
		return vehicles.stream()
				.map(Vehicle::getBodyType)
				.collect(Collectors.toSet());
	}

	@Override
	public Map<String, List<Vehicle>> findVehicleGrouppedByMake() {
		return vehicles.stream()
				.collect(Collectors.groupingBy(Vehicle::getMake));
	}

	@Override
	public List<Client> findTopClientsByPrices(List<Client> clients, int maxCount) {
		return clients
				.stream()
				.sorted((c1, c2) -> {
					BigDecimal c1Total = orders.stream()
							.filter(o -> o.getClientId() == c1.getId())
							.map(Order::getPrice)
							.reduce(BigDecimal.ZERO, BigDecimal::add);
					BigDecimal c2Total = orders.stream()
							.filter(o -> o.getClientId() == c2.getId())
							.map(Order::getPrice)
							.reduce(BigDecimal.ZERO, BigDecimal::add);

					return c2Total.compareTo(c1Total);
				}).limit(maxCount).toList();
	}

	@Override
	public List<Client> findClientsWithAveragePriceNoLessThan(List<Client> clients, int average) {
		return clients
				.stream()
				.filter(c -> {
					double averagePrice = orders.stream()
							.filter(o -> o.getClientId() == c.getId())
							.map(Order::getPrice)
							.mapToDouble(BigDecimal::doubleValue)
							.average().orElse(0);

					return averagePrice >= average;
				}).toList();
	}

	@Override
	public List<Vehicle> findMostOrderedVehicles(int maxCount) {
		return orders.stream()
				.filter(o -> vehicles.stream().anyMatch(v -> v.getId() == o.getVehicleId()))
				.collect(Collectors.groupingBy(Order::getVehicleId, Collectors.counting()))
				.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.limit(maxCount)
				.map(e -> vehicles.stream().filter(v -> v.getId() == e.getKey()).findFirst().orElse(null))
				.collect(Collectors.toList());
	}

}