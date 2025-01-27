/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.patient;

import java.util.List;
import java.util.Optional;

import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository class for <code>Patient</code> domain objects All method names are compliant
 * with Spring Data naming conventions so this interface can easily be extended for Spring
 * Data. See:
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Wick Dynex
 */
public interface PatientRepository extends JpaRepository<Patient, Integer> {

	/**
	 * Retrieve {@link Patient}s from the data store by user ID, returning all owners
	 * whose user ID <i>starts</i> with the given name.
	 * @param userID Value to search for
	 * @return a Collection of matching {@link Patient}s (or an empty Collection if none
	 * found)
	 */
	Page<Patient> findByUserIdStartingWith(String userId, Pageable pageable);

	/**
	 * Retrieve an {@link Patient} from the data store by id.
	 * <p>
	 * This method returns an {@link Optional} containing the {@link Patient} if found. If
	 * no {@link Patient} is found with the provided id, it will return an empty
	 * {@link Optional}.
	 * </p>
	 * @param id the id to search for
	 * @return an {@link Optional} containing the {@link Patient} if found, or an empty
	 * {@link Optional} if not found.
	 * @throws IllegalArgumentException if the id is null (assuming null is not a valid
	 * input for id)
	 */
	Optional<Patient> findById(@Nonnull Integer id);

	/**
	 * Returns all the Patients from data store
	 **/
	Page<Patient> findAll(Pageable pageable);

}
