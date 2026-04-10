import tkinter as tk
from tkinter import messagebox, ttk

VEHICLE_RATES = {'Economy': 10, 'Premium': 18, 'Suv': 25}
PEAK_HOURS = range(17, 21)
SURGE_MULTIPLIER = 1.5
CURRENCY = "₹"

ride_history = []  # stores all booked rides


def calculate_fare(km, vehicle_type, hour):
    if vehicle_type not in VEHICLE_RATES:
        raise ValueError(
            f"Service Not Available: '{vehicle_type}' is not valid.\n"
            f"Choose: Economy, Premium, SUV"
        )
    rate = VEHICLE_RATES[vehicle_type]
    base_fare = km * rate
    is_surge = hour in PEAK_HOURS
    multiplier = SURGE_MULTIPLIER if is_surge else 1.0
    final_fare = base_fare * multiplier
    return {
        "vehicle_type":  vehicle_type,
        "rate_per_km":   rate,
        "distance_km":   km,
        "base_fare":     base_fare,
        "surge_applied": is_surge,
        "multiplier":    multiplier,
        "final_fare":    final_fare,
        "hour":          hour,
    }


def main():
    root = tk.Tk()
    root.title("CityCab — FareCalc")
    root.configure(bg="black")
    
    # --- CHANGED: Maximizes the window on startup ---
    root.state('zoomed') 

    def lbl(parent, text, size=10, bold=False):
        return tk.Label(parent, text=text,
                        font=("Courier", size, "bold" if bold else "normal"),
                        bg="black", fg="white")

    def entry_field(parent, var):
        return tk.Entry(parent, textvariable=var, font=("Courier", 11),
                        bg="black", fg="white", insertbackground="white",
                        relief="solid", bd=1, width=34)

    pad = {"padx": 30, "pady": 5}

    # Title
    lbl(root, "CityCab — FareCalc", size=15, bold=True).pack(pady=(20, 2))
    tk.Frame(root, bg="white", height=1).pack(fill="x", padx=30, pady=6)

    # Inputs
    lbl(root, "Distance (km)").pack(anchor="w", padx=30)
    km_var = tk.StringVar()
    entry_field(root, km_var).pack(**pad)

    lbl(root, "Vehicle Type  (Economy / Premium / SUV)").pack(anchor="w", padx=30)
    vehicle_var = tk.StringVar()
    entry_field(root, vehicle_var).pack(**pad)

    lbl(root, "Pickup Hour  (0 - 23)").pack(anchor="w", padx=30)
    hour_var = tk.StringVar()
    entry_field(root, hour_var).pack(**pad)

    tk.Frame(root, bg="white", height=1).pack(fill="x", padx=30, pady=6)

    # Receipt label 
    receipt_var = tk.StringVar(value="")
    tk.Label(root, textvariable=receipt_var, font=("Courier", 10),
             bg="black", fg="white", justify="left", anchor="w").pack(padx=30, fill="x")

    # Buttons row
    btn_frame = tk.Frame(root, bg="black")
    btn_frame.pack(pady=10)

    def calculate():
        try:
            km_str = km_var.get().strip()
            if not km_str: raise ValueError
            km = float(km_str)
            if km <= 0: raise ValueError
        except ValueError:
            messagebox.showerror("Error", "Enter a valid positive distance.")
            return

        vehicle = vehicle_var.get().strip().title()

        try:
            hour_str = hour_var.get().strip()
            if not hour_str: raise ValueError
            hour = int(hour_str)
            if not (0 <= hour <= 23): raise ValueError
        except ValueError:
            messagebox.showerror("Error", "Enter a valid hour between 0 and 23.")
            return

        try:
            info = calculate_fare(km, vehicle, hour)
        except ValueError as e:
            messagebox.showerror("Error", str(e))
            return

        surge_line = ""
        if info["surge_applied"]:
            surge_amt = info["base_fare"] * (info["multiplier"] - 1)
            surge_line = f"  Surge Added   : {CURRENCY}{surge_amt:.2f}  (x1.5 Peak)\n"

        time_status = "PEAK HOURS (Surge)" if info["surge_applied"] else "Normal"

        text = (
            f"  Vehicle       : {info['vehicle_type']}\n"
            f"  Distance      : {info['distance_km']:.1f} km\n"
            f"  Rate/km       : {CURRENCY}{info['rate_per_km']}\n"
            f"  Pickup Hour   : {info['hour']:02d}:00  ({time_status})\n"
            f"  Base Fare     : {CURRENCY}{info['base_fare']:.2f}\n"
            f"{surge_line}"
            f"  {'─'*32}\n"
            f"  TOTAL         : {CURRENCY}{info['final_fare']:.2f}"
        )
        receipt_var.set(text)

        ride_history.append(info)
        refresh_history()

    def clear_fields():
        km_var.set("")
        vehicle_var.set("")
        hour_var.set("")
        receipt_var.set("")

    btn_style = dict(font=("Courier", 11, "bold"), bd=0,
                     padx=14, pady=8, cursor="hand2")

    tk.Button(btn_frame, text="Calculate Fare",
              bg="white", fg="black", activebackground="#dddddd",
              command=calculate, **btn_style).grid(row=0, column=0, padx=8)

    tk.Button(btn_frame, text="Clear / New Ride",
              bg="black", fg="white", activebackground="#222222",
              relief="solid",
              command=clear_fields, **btn_style).grid(row=0, column=1, padx=8)

    # Ride History 
    tk.Frame(root, bg="white", height=1).pack(fill="x", padx=30, pady=(8, 4))
    lbl(root, f"Ride History", size=11, bold=True).pack(anchor="w", padx=30)

    summary_var = tk.StringVar(value="No rides booked yet.")
    tk.Label(root, textvariable=summary_var, font=("Courier", 9),
             bg="black", fg="white", anchor="w").pack(anchor="w", padx=30)

    # Table frame
    table_frame = tk.Frame(root, bg="black")
    # --- UPDATED: Expand set to True to fill the screen ---
    table_frame.pack(fill="both", expand=True, padx=30, pady=(4, 16))

    style = ttk.Style()
    style.theme_use("clam")
    style.configure("Mono.Treeview",
                    background="black", foreground="white",
                    fieldbackground="black", rowheight=25,
                    font=("Courier", 10))
    style.configure("Mono.Treeview.Heading",
                    background="black", foreground="white",
                    font=("Courier", 10, "bold"), relief="flat")

    cols = ("#", "Vehicle", "Dist(km)", "Hour", "Base", "Surge", "Total")
    tree = ttk.Treeview(table_frame, columns=cols, show="headings", style="Mono.Treeview")

    # Adjusted column widths for a larger screen
    col_widths = [40, 150, 120, 100, 120, 100, 120]
    for col, w in zip(cols, col_widths):
        tree.heading(col, text=col)
        tree.column(col, width=w, anchor="center")

    scrollbar = tk.Scrollbar(table_frame, orient="vertical", command=tree.yview)
    tree.configure(yscrollcommand=scrollbar.set)
    tree.pack(side="left", fill="both", expand=True)
    scrollbar.pack(side="right", fill="y")

    def refresh_history():
        for row in tree.get_children():
            tree.delete(row)

        total_spent = 0.0
        for i, info in enumerate(ride_history, 1):
            surge_flag = "Yes" if info["surge_applied"] else "No"
            tree.insert("", "end", values=(
                i,
                info["vehicle_type"],
                f"{info['distance_km']:.1f}",
                f"{info['hour']:02d}:00",
                f"{CURRENCY}{info['base_fare']:.0f}",
                surge_flag,
                f"{CURRENCY}{info['final_fare']:.2f}",
            ))
            total_spent += info["final_fare"]

        count = len(ride_history)
        summary_var.set(
            f"Total rides: {count}   |   Total spent: {CURRENCY}{total_spent:.2f}"
        )

    root.mainloop()


if __name__ == "__main__":
    main()
