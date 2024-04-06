package junglamar.banco;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Cliente {
    String nombres;
    String apellidos;
    int edad;
    String tipoCuenta;

    public Cliente(String nombres, String apellidos, int edad, String tipoCuenta) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.edad = edad;
        this.tipoCuenta = tipoCuenta;
    }
}

class Cuenta {
    Cliente cliente;
    double saldo;

    public Cuenta(Cliente cliente, double saldo) {
        this.cliente = cliente;
        this.saldo = saldo;
    }

    public void depositar(double monto) {
        if (cliente.tipoCuenta.equalsIgnoreCase("Corriente")) {
            if (monto < 500000) {
                saldo -= 7000;
            } else if (monto >= 500000 && monto < 2000000) {
                saldo -= (5000 + 0.02 * monto);
            } else if (monto >= 2000000 && monto <= 10000000) {
                saldo -= (4000 + 0.02 * monto);
            } else {
                saldo -= (0.033 * monto);
            }
        } else if (cliente.tipoCuenta.equalsIgnoreCase("Ahorro")) {
            if (monto >= 500000 && monto < 2000000) {
                saldo -= (3000 + 0.01 * monto);
            } else if (monto >= 2000000 && monto <= 10000000) {
                saldo -= (2000 + 0.005 * monto);
            } else if (monto > 10000000 && monto < 100000000) {
                saldo -= (0.018 * monto);
            } else if (monto >= 100000000) {
                saldo -= (0.02 * monto);
            }
        }

        this.saldo += monto;
    }

    public void retirar(double monto) {
        if (monto > saldo) {
            System.out.println("Saldo insuficiente para realizar el retiro.");
            return;
        }

        if (cliente.tipoCuenta.equalsIgnoreCase("Corriente")) {
            saldo -= 3000; // Se cobra 3000 pesos por uso de cheque emitido
            System.out.println("Retiro realizado con éxito.");
        } else if (cliente.tipoCuenta.equalsIgnoreCase("Ahorro")) {
            saldo -= 4500; // Se cobra 4500 pesos por retiro en cajero ajeno al banco
            System.out.println("Retiro realizado con éxito.");
        }

        this.saldo -= monto;
    }

    public void transferir(Cuenta destino, double monto) {
        if (monto > saldo) {
            System.out.println("Saldo insuficiente para realizar la transferencia.");
            return;
        }

        saldo -= monto;
        destino.saldo += monto;

        System.out.println("Transferencia exitosa de " + monto + " pesos a la cuenta de "
                + destino.cliente.nombres + " " + destino.cliente.apellidos);
    }
    
    public void cierreDeMes() {
        if (cliente.tipoCuenta.equalsIgnoreCase("Corriente")) {
            double comisionMantenimiento = saldo * 0.015; // Tasa de mantenimiento del 1.5%
            saldo -= comisionMantenimiento;
            System.out.println("Se aplicó una comisión de " + comisionMantenimiento + " pesos por mantenimiento de cuenta corriente.");
        }

        if (cliente.tipoCuenta.equalsIgnoreCase("Ahorro")) {
            double interes = saldo * 0.022 / 12; // Interés mensual del 2.2% anual
            saldo += interes;
            System.out.println("Se aplicó un interés de " + interes + " pesos por cuenta de ahorro.");
        }

        System.out.println("Estado de cuenta para " + cliente.nombres + " " + cliente.apellidos + ": " + saldo);
    }
}

public class Banco {
    static List<Cuenta> cuentas = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("Menú:");
            System.out.println("1. Aperturas de Cuentas: Ahorro y Corriente");
            System.out.println("2. Transferencias");
            System.out.println("3. Cajero Automático");
            System.out.println("4. Cierre de mes (Estado de Cuenta)");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    abrirCuenta();
                    break;
                case 2:
                    realizarTransferencia();
                    break;
                case 3:
                    menuCajeroAutomatico();
                    break;
                case 4:
                    cierreDeMes();
                    break;
                case 0:
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opción inválida.");
                    break;
            }
        } while (opcion != 0);
    }

    public static void abrirCuenta() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingrese los datos del cliente:");

        System.out.print("Nombres: ");
        String nombres = scanner.nextLine();

        System.out.print("Apellidos: ");
        String apellidos = scanner.nextLine();

        System.out.print("Edad: ");
        int edad = scanner.nextInt();

        System.out.print("Tipo de cuenta (Ahorro/Corriente): ");
        String tipoCuenta = scanner.next();

        Cliente cliente = new Cliente(nombres, apellidos, edad, tipoCuenta);

        System.out.print("Monto de apertura: ");
        double montoApertura = scanner.nextDouble();

        if (tipoCuenta.equalsIgnoreCase("Corriente") && montoApertura < 200000) {
            System.out.println("El monto mínimo de apertura para cuenta corriente es de 200 mil pesos.");
        } else {
            Cuenta cuenta = new Cuenta(cliente, montoApertura);
            cuentas.add(cuenta);
            System.out.println("Cuenta abierta con éxito.");
            System.out.println("Cliente: " + cliente.nombres + " " + cliente.apellidos);
            System.out.println("Saldo inicial: " + cuenta.saldo);
        }
    }

    public static void realizarTransferencia() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingrese los datos de la transferencia:");

        System.out.print("Nombres del titular de la cuenta de origen: ");
        String nombresOrigen = scanner.nextLine();

        System.out.print("Apellidos del titular de la cuenta de origen: ");
        String apellidosOrigen = scanner.nextLine();

        System.out.print("Nombres del titular de la cuenta de destino: ");
        String nombresDestino = scanner.nextLine();

        System.out.print("Apellidos del titular de la cuenta de destino: ");
        String apellidosDestino = scanner.nextLine();

        Cuenta cuentaOrigen = buscarCuenta(nombresOrigen, apellidosOrigen);
        Cuenta cuentaDestino = buscarCuenta(nombresDestino, apellidosDestino);

        if (cuentaOrigen == null || cuentaDestino == null) {
            System.out.println("No se encontraron las cuentas especificadas.");
            return;
        }

        System.out.print("Monto a transferir: ");
        double monto = scanner.nextDouble();

        cuentaOrigen.transferir(cuentaDestino, monto);
    }

    public static void menuCajeroAutomatico() {
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("Menú Cajero Automático:");
            System.out.println("1. Retirar dinero");
            System.out.println("2. Depositar dinero");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    retirarDinero();
                    break;
                case 2:
                    depositarDinero();
                    break;
                case 0:
                    System.out.println("Volviendo al menú principal...");
                    break;
                default:
                    System.out.println("Opción inválida.");
                    break;
            }
        } while (opcion != 0);
    }

    public static void retirarDinero() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingrese los datos para retirar dinero:");

        System.out.print("Nombres del titular de la cuenta: ");
        String nombres = scanner.nextLine();

        System.out.print("Apellidos del titular de la cuenta: ");
        String apellidos = scanner.nextLine();

        Cuenta cuenta = buscarCuenta(nombres, apellidos);

        if (cuenta == null) {
            System.out.println("No se encontró la cuenta especificada.");
            return;
        }

        System.out.print("Monto a retirar: ");
        double monto = scanner.nextDouble();

        cuenta.retirar(monto);
    }

    public static void depositarDinero() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingrese los datos para depositar dinero:");

        System.out.print("Nombres del titular de la cuenta: ");
        String nombres = scanner.nextLine();

        System.out.print("Apellidos del titular de la cuenta: ");
        String apellidos = scanner.nextLine();

        Cuenta cuenta = buscarCuenta(nombres, apellidos);

        if (cuenta == null) {
            System.out.println("No se encontró la cuenta especificada.");
            return;
        }

        System.out.print("Monto a depositar: ");
        double monto = scanner.nextDouble();

        cuenta.depositar(monto);
    }

    public static Cuenta buscarCuenta(String nombres, String apellidos) {
        for (Cuenta cuenta : cuentas) {
            if (cuenta.cliente.nombres.equalsIgnoreCase(nombres) && cuenta.cliente.apellidos.equalsIgnoreCase(apellidos)) {
                return cuenta;
            }
        }
        // Retorna null si no se encuentra la cuenta
        return null;
    }
    
    public static void cierreDeMes() {
        System.out.println("Realizando cierre de mes...");
        for (Cuenta cuenta : cuentas) {
            cuenta.cierreDeMes();
        }
        System.out.println("Cierre de mes completado.");
    }
}
