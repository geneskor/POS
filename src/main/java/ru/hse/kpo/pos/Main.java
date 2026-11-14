package ru.hse.kpo.pos;

import com.google.inject.Guice;
import com.google.inject.Injector;
import ru.hse.kpo.pos.infrastructure.console.ConsoleApp;
import ru.hse.kpo.pos.infrastructure.di.PosModule;

public final class Main {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new PosModule());
        injector.getInstance(ConsoleApp.class).run();
    }
}
