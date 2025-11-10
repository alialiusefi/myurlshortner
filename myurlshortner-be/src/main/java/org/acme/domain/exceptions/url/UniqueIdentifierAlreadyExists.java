package org.acme.domain.exceptions.url;

import org.acme.domain.exceptions.DomainException;

public class UniqueIdentifierAlreadyExists extends DomainException {
    public UniqueIdentifierAlreadyExists() {
        super("UNIQUE_IDENTIFIER_ALREADY_EXISTS", "The provided unique identifier already exists.");
    }
}
