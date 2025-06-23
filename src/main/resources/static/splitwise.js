// Splitwise Expense Splitting Module (User-Friendly UI)
class Splitwise {
    constructor() {
        this.groups = [];
        this.members = [];
        this.expenses = [];
        this.balances = {};
        this.currentGroupId = null;
        this.currentView = 'dashboard'; // dashboard, groups, members, expenses, balances
        this.initializeElements();
        this.bindEvents();
        this.testApiConnection().then(success => {
            if (success) {
                this.fetchGroups();
            }
        });
    }

    initializeElements() {
        // Navigation elements
        this.navContainer = document.getElementById('splitwise-nav');
        this.contentContainer = document.getElementById('splitwise-content');

        // Status elements
        this.statusMessage = document.getElementById('splitwise-status');
    }

    bindEvents() {
        // Navigation events
        if (this.navContainer) {
            this.navContainer.addEventListener('click', (e) => {
                if (e.target.classList.contains('nav-btn')) {
                    const view = e.target.dataset.view;
                    this.switchView(view);
                }
            });
        }

        // Dynamic events
        document.addEventListener('click', (e) => {
            if (e.target.closest('.delete-group')) {
                const groupId = e.target.closest('.delete-group').dataset.groupId;
                this.deleteGroup(groupId);
            }
            if (e.target.closest('.delete-member')) {
                const memberId = e.target.closest('.delete-member').dataset.memberId;
                this.deleteMember(memberId);
            }
            if (e.target.closest('.delete-expense')) {
                const expenseId = e.target.closest('.delete-expense').dataset.expenseId;
                this.deleteExpense(expenseId);
            }
            if (e.target.closest('.select-group')) {
                const groupId = e.target.closest('.select-group').dataset.groupId;
                this.selectGroup(groupId);
            }
            if (e.target.closest('.view-expense-details')) {
                const expenseId = e.target.closest('.view-expense-details').dataset.expenseId;
                this.showExpenseDetails(expenseId);
            }
        });
    }

    // Navigation and View Management
    switchView(view) {
        this.currentView = view;
        this.updateNavigation();
        this.updateContent();
    }

    updateNavigation() {
        if (!this.navContainer) return;

        // Update active nav button
        this.navContainer.querySelectorAll('.nav-btn').forEach(btn => {
            btn.classList.remove('bg-blue-600', 'text-white');
            btn.classList.add('bg-gray-100', 'text-gray-700', 'hover:bg-gray-200');
        });

        const activeBtn = this.navContainer.querySelector(`[data-view="${this.currentView}"]`);
        if (activeBtn) {
            activeBtn.classList.remove('bg-gray-100', 'text-gray-700', 'hover:bg-gray-200');
            activeBtn.classList.add('bg-blue-600', 'text-white');
        }
    }

    updateContent() {
        switch (this.currentView) {
            case 'dashboard':
                this.showDashboardView();
                break;
            case 'groups':
                this.showGroupsView();
                break;
            case 'members':
                this.showMembersView();
                break;
            case 'expenses':
                this.showExpensesView();
                break;
            case 'balances':
                this.showBalancesView();
                break;
        }
    }

    showDashboardView() {
        if (!this.contentContainer) return;

        this.contentContainer.innerHTML = `
            <div class="space-y-6">
                <!-- Welcome Section -->
                <div class="bg-gradient-to-r from-blue-500 to-purple-600 rounded-lg shadow-lg p-6 text-white">
                    <h2 class="text-2xl font-bold mb-2">Welcome to Splitwise</h2>
                    <p class="text-blue-100">Split expenses with friends and family easily!</p>
                </div>

                <!-- Quick Actions -->
                <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700 p-6 hover:shadow-md transition-shadow">
                        <div class="flex items-center mb-4">
                            <div class="w-10 h-10 bg-blue-100 dark:bg-blue-900 rounded-lg flex items-center justify-center mr-3">
                                <span class="text-blue-600 dark:text-blue-400 text-xl">🏠</span>
                            </div>
                            <h3 class="text-lg font-semibold text-gray-900 dark:text-white">Groups</h3>
                        </div>
                        <p class="text-gray-600 dark:text-gray-400 mb-4">Create and manage expense groups</p>
                        <button onclick="splitwise.switchView('groups')" class="w-full px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors">
                            Manage Groups
                        </button>
                    </div>

                    <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700 p-6 hover:shadow-md transition-shadow">
                        <div class="flex items-center mb-4">
                            <div class="w-10 h-10 bg-green-100 dark:bg-green-900 rounded-lg flex items-center justify-center mr-3">
                                <span class="text-green-600 dark:text-green-400 text-xl">💰</span>
                            </div>
                            <h3 class="text-lg font-semibold text-gray-900 dark:text-white">Add Expense</h3>
                        </div>
                        <p class="text-gray-600 dark:text-gray-400 mb-4">Record a new shared expense</p>
                        <button onclick="splitwise.switchView('expenses')" class="w-full px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors">
                            Add Expense
                        </button>
                    </div>

                    <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700 p-6 hover:shadow-md transition-shadow">
                        <div class="flex items-center mb-4">
                            <div class="w-10 h-10 bg-purple-100 dark:bg-purple-900 rounded-lg flex items-center justify-center mr-3">
                                <span class="text-purple-600 dark:text-purple-400 text-xl">⚖️</span>
                            </div>
                            <h3 class="text-lg font-semibold text-gray-900 dark:text-white">Balances</h3>
                        </div>
                        <p class="text-gray-600 dark:text-gray-400 mb-4">View who owes what to whom</p>
                        <button onclick="splitwise.switchView('balances')" class="w-full px-4 py-2 bg-purple-600 text-white rounded-md hover:bg-purple-700 transition-colors">
                            View Balances
                        </button>
                    </div>
                </div>

                <!-- Recent Activity -->
                <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700 p-6">
                    <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Recent Activity</h3>
                    <div class="space-y-3">
                        ${this.renderRecentActivity()}
                    </div>
                </div>
            </div>
        `;
    }

    showGroupsView() {
        if (!this.contentContainer) return;

        this.contentContainer.innerHTML = `
            <div class="space-y-6">
                <!-- Header -->
                <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700 p-6">
                    <h2 class="text-xl font-bold text-gray-900 dark:text-white mb-2">Manage Groups</h2>
                    <p class="text-gray-600 dark:text-gray-400">Create groups to organize your shared expenses</p>
                </div>

                <!-- Create Group Form -->
                <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700 p-6">
                    <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Create New Group</h3>
                    <div class="flex gap-3">
                        <input type="text" id="group-name" placeholder="Enter group name (e.g., Roommates, Trip to Bali)" 
                               class="flex-1 px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white">
                        <button id="add-group-btn" 
                                class="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors">
                            Create Group
                        </button>
                    </div>
                </div>
                
                <!-- Groups List -->
                <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700 p-6">
                    <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Your Groups</h3>
                    <div id="groups-list" class="space-y-3">
                        ${this.renderGroupsList()}
                    </div>
                </div>
            </div>
        `;

        // Re-bind elements
        this.groupNameInput = document.getElementById('group-name');
        this.addGroupBtn = document.getElementById('add-group-btn');
        this.groupsList = document.getElementById('groups-list');

        // Re-bind event listeners
        if (this.addGroupBtn) {
            this.addGroupBtn.addEventListener('click', () => this.addGroup());
        }
    }

    showMembersView() {
        if (!this.contentContainer || !this.currentGroupId) {
            this.showMessage('Please select a group first', 'warning');
            this.switchView('groups');
            return;
        }

        this.contentContainer.innerHTML = `
            <div class="space-y-6">
                <!-- Header -->
                <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700 p-6">
                    <div class="flex justify-between items-center">
                        <div>
                            <h2 class="text-xl font-bold text-gray-900 dark:text-white">Group Members</h2>
                            <p class="text-gray-600 dark:text-gray-400">Group: ${this.getCurrentGroupName()}</p>
                        </div>
                        <button onclick="splitwise.switchView('groups')" class="px-4 py-2 bg-gray-500 text-white rounded-md hover:bg-gray-600 transition-colors">
                            Back to Groups
                        </button>
                    </div>
                </div>

                <!-- Add Member Form -->
                <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700 p-6">
                    <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Add New Member</h3>
                    <div class="flex gap-3">
                        <input type="text" id="member-name" placeholder="Enter member name" 
                               class="flex-1 px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white">
                        <button id="add-member-btn" 
                                class="px-6 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500 transition-colors">
                            Add Member
                        </button>
                    </div>
                </div>
                
                <!-- Members List -->
                <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700 p-6">
                    <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Members List</h3>
                    <div id="members-list" class="space-y-3">
                        ${this.renderMembersList()}
                    </div>
                </div>
            </div>
        `;

        // Re-bind elements
        this.memberNameInput = document.getElementById('member-name');
        this.addMemberBtn = document.getElementById('add-member-btn');
        this.membersList = document.getElementById('members-list');

        // Re-bind event listeners
        if (this.addMemberBtn) {
            this.addMemberBtn.addEventListener('click', () => this.addMember());
        }
    }

    showExpensesView() {
        if (!this.contentContainer || !this.currentGroupId) {
            this.showMessage('Please select a group first', 'warning');
            this.switchView('groups');
            return;
        }

        this.contentContainer.innerHTML = `
            <div class="space-y-6">
                <!-- Header -->
                <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700 p-6">
                    <div class="flex justify-between items-center">
                        <div>
                            <h2 class="text-xl font-bold text-gray-900 dark:text-white">Expenses</h2>
                            <p class="text-gray-600 dark:text-gray-400">Group: ${this.getCurrentGroupName()}</p>
                        </div>
                        <div class="flex gap-2">
                            <button onclick="splitwise.switchView('members')" class="px-4 py-2 bg-gray-500 text-white rounded-md hover:bg-gray-600 transition-colors">
                                Manage Members
                            </button>
                            <button onclick="splitwise.switchView('groups')" class="px-4 py-2 bg-gray-500 text-white rounded-md hover:bg-gray-600 transition-colors">
                                Back to Groups
                            </button>
                        </div>
                    </div>
                </div>

                <!-- Add Expense Form -->
                <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700 p-6">
                    <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Add New Expense</h3>
                    
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                        <div>
                            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Description</label>
                            <input type="text" id="expense-description" placeholder="What was this expense for?" 
                                   class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white">
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Amount (RM)</label>
                            <input type="number" id="expense-amount" placeholder="0.00" step="0.01" min="0" 
                                   class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white">
                        </div>
                    </div>
                    
                    <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
                        <div>
                            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Paid By</label>
                            <select id="expense-paid-by" 
                                    class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white">
                                <option value="">Select member</option>
                                ${this.renderMemberOptions()}
                            </select>
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Split Type</label>
                            <select id="expense-split-type" 
                                    class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white">
                                <option value="EQUAL">Equal Split</option>
                                <option value="PERCENTAGE">Percentage</option>
                                <option value="CUSTOM">Custom Amount</option>
                            </select>
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Group</label>
                            <select id="expense-group" 
                                    class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white">
                                ${this.renderGroupOptions()}
                            </select>
                        </div>
                    </div>
                    
                    <div id="expense-split-members" class="mb-4">
                        ${this.renderSplitMembersUI()}
                    </div>
                    
                    <button id="add-expense-btn" 
                            class="w-full px-4 py-2 bg-purple-600 text-white rounded-md hover:bg-purple-700 focus:outline-none focus:ring-2 focus:ring-purple-500 transition-colors">
                        Add Expense
                    </button>
                </div>
                
                <!-- Expenses List -->
                <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700 p-6">
                    <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Recent Expenses</h3>
                    <div id="expenses-list" class="space-y-3">
                        ${this.renderExpensesList()}
                    </div>
                </div>
            </div>
        `;

        // Re-bind elements
        this.expenseDescription = document.getElementById('expense-description');
        this.expenseAmount = document.getElementById('expense-amount');
        this.expensePaidBy = document.getElementById('expense-paid-by');
        this.expenseGroup = document.getElementById('expense-group');
        this.expenseSplitType = document.getElementById('expense-split-type');
        this.expenseSplitMembers = document.getElementById('expense-split-members');
        this.addExpenseBtn = document.getElementById('add-expense-btn');
        this.expensesList = document.getElementById('expenses-list');

        if (this.addExpenseBtn) {
            this.addExpenseBtn.addEventListener('click', () => this.addExpense());
        }
        if (this.expenseGroup) {
            this.expenseGroup.addEventListener('change', () => this.updateSplitMembers());
        }
        if (this.expenseSplitType) {
            this.expenseSplitType.addEventListener('change', () => this.updateSplitUI());
        }
    }

    showBalancesView() {
        if (!this.contentContainer || !this.currentGroupId) {
            this.showMessage('Please select a group first', 'warning');
            this.switchView('groups');
            return;
        }

        this.contentContainer.innerHTML = `
            <div class="space-y-6">
                <!-- Header -->
                <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700 p-6">
                    <div class="flex justify-between items-center">
                        <div>
                            <h2 class="text-xl font-bold text-gray-900 dark:text-white">Group Balances</h2>
                            <p class="text-gray-600 dark:text-gray-400">Group: ${this.getCurrentGroupName()}</p>
                        </div>
                        <button onclick="splitwise.switchView('expenses')" class="px-4 py-2 bg-gray-500 text-white rounded-md hover:bg-gray-600 transition-colors">
                            Back to Expenses
                        </button>
                    </div>
                </div>

                <!-- Balances List -->
                <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700 p-6">
                    <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Current Balances</h3>
                    <div id="balances-list" class="space-y-3">
                        ${this.renderBalancesList()}
                    </div>
                </div>
                
                <!-- Settle Up -->
                <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700 p-6">
                    <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Settle Up</h3>
                    <p class="text-gray-600 dark:text-gray-400 mb-4">Mark expenses as settled when payments are made.</p>
                    <button id="settle-up-btn" 
                            class="px-4 py-2 bg-orange-600 text-white rounded-md hover:bg-orange-700 focus:outline-none focus:ring-2 focus:ring-orange-500 transition-colors">
                        Settle All Balances
                    </button>
                </div>
            </div>
        `;

        // Re-bind elements
        this.balancesList = document.getElementById('balances-list');
        this.settleUpBtn = document.getElementById('settle-up-btn');

        if (this.settleUpBtn) {
            this.settleUpBtn.addEventListener('click', () => this.settleUp());
        }
    }

    // Helper methods
    getCurrentGroupName() {
        const group = this.groups.find(g => g.id == this.currentGroupId);
        return group ? group.name : 'Unknown Group';
    }

    renderRecentActivity() {
        if (this.groups.length === 0) {
            return '<p class="text-gray-500 dark:text-gray-400 text-center py-4">No activity yet. Create your first group to get started!</p>';
        }

        const recentGroups = this.groups.slice(0, 3);
        return recentGroups.map(group => `
            <div class="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-700 rounded-lg">
                <div class="flex items-center">
                    <div class="w-8 h-8 bg-blue-100 dark:bg-blue-900 rounded-full flex items-center justify-center mr-3">
                        <span class="text-blue-600 dark:text-blue-400 text-sm">🏠</span>
                    </div>
                    <div>
                        <p class="font-medium text-gray-900 dark:text-white">${group.name}</p>
                        <p class="text-sm text-gray-500 dark:text-gray-400">Created ${group.createdAt ? new Date(group.createdAt).toLocaleDateString() : ''}</p>
                    </div>
                </div>
                <button onclick="splitwise.selectGroup(${group.id})" class="px-3 py-1 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 transition-colors">
                    Select
                </button>
            </div>
        `).join('');
    }

    renderGroupsList() {
        if (this.groups.length === 0) {
            return '<p class="text-gray-500 dark:text-gray-400 text-center py-4">No groups created yet. Create your first group above!</p>';
        }

        return this.groups.map(group => `
            <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4 border border-gray-200 dark:border-gray-600">
                <div class="flex justify-between items-center">
                    <div class="flex-1">
                        <h4 class="font-semibold text-gray-900 dark:text-white">${group.name}</h4>
                        <p class="text-sm text-gray-500 dark:text-gray-400">Created ${group.createdAt ? new Date(group.createdAt).toLocaleDateString() : ''}</p>
                    </div>
                    <div class="flex items-center gap-2">
                        <button class="select-group px-4 py-2 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 transition-colors" data-group-id="${group.id}">
                            Select Group
                        </button>
                        <button class="delete-group text-red-500 hover:text-red-700 dark:text-red-400 dark:hover:text-red-300 p-2" data-group-id="${group.id}" title="Delete Group">
                            🗑️
                        </button>
                    </div>
                </div>
            </div>
        `).join('');
    }

    renderMembersList() {
        if (this.members.length === 0) {
            return '<p class="text-gray-500 dark:text-gray-400 text-center py-4">No members in this group. Add members above!</p>';
        }

        return this.members.map(member => `
            <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4 border border-gray-200 dark:border-gray-600">
                <div class="flex justify-between items-center">
                    <div>
                        <h4 class="font-semibold text-gray-900 dark:text-white">${member.name}</h4>
                        <p class="text-sm text-gray-500 dark:text-gray-400">Added ${member.createdAt ? new Date(member.createdAt).toLocaleDateString() : ''}</p>
                    </div>
                    <button class="delete-member text-red-500 hover:text-red-700 dark:text-red-400 dark:hover:text-red-300 p-2" data-member-id="${member.id}" title="Delete Member">
                        🗑️
                    </button>
                </div>
            </div>
        `).join('');
    }

    renderExpensesList() {
        if (this.expenses.length === 0) {
            return '<p class="text-gray-500 dark:text-gray-400 text-center py-4">No expenses in this group. Add expenses above!</p>';
        }

        return this.expenses.map(expense => `
            <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4 border border-gray-200 dark:border-gray-600">
                <div class="flex justify-between items-start">
                    <div class="flex-1">
                        <h4 class="font-semibold text-gray-900 dark:text-white">${expense.description}</h4>
                        <p class="text-sm text-gray-500 dark:text-gray-400">Paid by: ${expense.paidBy ? expense.paidBy.name : 'Unknown'}</p>
                        <p class="text-sm text-gray-500 dark:text-gray-400">Split Type: ${expense.splitType}</p>
                    </div>
                    <div class="text-right">
                        <p class="font-semibold text-gray-900 dark:text-white">RM ${expense.amount}</p>
                        <button class="delete-expense text-red-500 hover:text-red-700 dark:text-red-400 dark:hover:text-red-300 text-sm" data-expense-id="${expense.id}" title="Delete Expense">
                            🗑️
                        </button>
                    </div>
                </div>
            </div>
        `).join('');
    }

    renderBalancesList() {
        if (this.members.length === 0) {
            return '<p class="text-gray-500 dark:text-gray-400 text-center py-4">No members in this group. Add members first!</p>';
        }

        return this.members.map(member => {
            const balance = this.balances && this.balances[member.id] !== undefined ? parseFloat(this.balances[member.id]) : 0;
            const balanceStr = balance >= 0 ? `RM ${balance.toFixed(2)}` : `-RM ${Math.abs(balance).toFixed(2)}`;
            const balanceColor = balance > 0 ? 'text-green-600 dark:text-green-400' : (balance < 0 ? 'text-red-600 dark:text-red-400' : 'text-gray-900 dark:text-white');
            return `
                <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4 border border-gray-200 dark:border-gray-600">
                    <div class="flex justify-between items-center">
                        <div>
                            <h4 class="font-semibold text-gray-900 dark:text-white">${member.name}</h4>
                        </div>
                        <div class="text-right">
                            <span class="text-sm text-gray-500 dark:text-gray-400">Balance:</span>
                            <span class="font-semibold ${balanceColor} ml-2">${balanceStr}</span>
                        </div>
                    </div>
                </div>
            `;
        }).join('');
    }

    renderMemberOptions() {
        return this.members.map(member =>
            `<option value="${member.id}">${member.name}</option>`
        ).join('');
    }

    renderGroupOptions() {
        return this.groups.map(group =>
            `<option value="${group.id}" ${group.id == this.currentGroupId ? 'selected' : ''}>${group.name}</option>`
        ).join('');
    }

    renderSplitMembersUI() {
        if (this.members.length === 0) {
            return '<p class="text-gray-500 dark:text-gray-400 text-center py-4">No members in this group. Add members first!</p>';
        }

        return this.members.map(member => `
            <div class="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-700 rounded-lg mb-2">
                <div class="flex items-center">
                    <input type="checkbox" value="${member.id}" class="mr-3" checked>
                    <span class="text-gray-900 dark:text-white">${member.name}</span>
                </div>
                <input type="number" placeholder="0.00" step="0.01" min="0" 
                       class="w-24 px-2 py-1 border border-gray-300 dark:border-gray-600 rounded text-sm dark:bg-gray-700 dark:text-white" disabled>
            </div>
        `).join('');
    }

    // API methods
    async apiGet(url) {
        try {
            const res = await fetch(url);
            if (!res.ok) {
                const errorData = await res.json().catch(() => ({ error: 'Unknown error' }));
                throw new Error(errorData.error || 'Request failed');
            }
            return res.json();
        } catch (error) {
            console.error('API GET Error:', error);
            throw error;
        }
    }

    async apiPost(url, data) {
        try {
            console.log('Sending POST request to:', url);
            console.log('Request data:', data);

            const res = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(data)
            });

            console.log('Response status:', res.status);
            console.log('Response headers:', res.headers);

            if (!res.ok) {
                let errorMessage = 'Request failed';
                try {
                    const errorData = await res.json();
                    errorMessage = errorData.error || `HTTP ${res.status}: ${res.statusText}`;
                } catch (parseError) {
                    errorMessage = `HTTP ${res.status}: ${res.statusText}`;
                }
                throw new Error(errorMessage);
            }

            const responseData = await res.json();
            console.log('Response data:', responseData);
            return responseData;
        } catch (error) {
            console.error('API POST Error:', error);
            throw error;
        }
    }

    async apiDelete(url) {
        try {
            const res = await fetch(url, { method: 'DELETE' });
            if (!res.ok) {
                const errorData = await res.json().catch(() => ({ error: 'Unknown error' }));
                throw new Error(errorData.error || 'Delete failed');
            }
        } catch (error) {
            console.error('API DELETE Error:', error);
            throw error;
        }
    }

    // Test API connection
    async testApiConnection() {
        try {
            console.log('Testing API connection...');
            const response = await this.apiGet('/api/splitwise/health');
            console.log('API connection successful:', response);
            this.showMessage('API connection successful!', 'success');
            return true;
        } catch (error) {
            console.error('API connection failed:', error);
            this.showMessage('API connection failed: ' + error.message, 'error');
            return false;
        }
    }

    // Core functionality
    async fetchGroups() {
        try {
            this.groups = await this.apiGet('/api/splitwise/groups');
            this.updateContent();
        } catch (e) {
            this.showMessage('Failed to fetch groups: ' + e.message, 'error');
        }
    }

    async addGroup() {
        const name = this.groupNameInput.value.trim();
        if (!name) return this.showMessage('Please enter a group name', 'warning');

        try {
            await this.apiPost('/api/splitwise/groups', { name: name });
            this.groupNameInput.value = '';
            this.showMessage('Group created successfully!', 'success');
            this.fetchGroups();
        } catch (e) {
            this.showMessage('Failed to add group: ' + e.message, 'error');
        }
    }

    async deleteGroup(groupId) {
        if (!confirm('Are you sure you want to delete this group? This will also delete all members and expenses.')) return;

        try {
            await this.apiDelete(`/api/splitwise/groups/${groupId}`);
            this.showMessage('Group deleted successfully!', 'success');
            if (this.currentGroupId == groupId) {
                this.currentGroupId = null;
            }
            this.fetchGroups();
        } catch (e) {
            this.showMessage('Failed to delete group: ' + e.message, 'error');
        }
    }

    selectGroup(groupId) {
        this.currentGroupId = groupId;
        this.showMessage(`Selected group: ${this.getCurrentGroupName()}`, 'success');
        this.refreshGroupData(groupId);
        this.switchView('expenses');
    }

    async refreshGroupData(groupId) {
        if (!groupId) return;

        // Show a loading indicator if you have one
        try {
            const [members, expenses, balances] = await Promise.all([
                this.apiGet(`/api/splitwise/groups/${groupId}/members`),
                this.apiGet(`/api/splitwise/groups/${groupId}/expenses`),
                this.apiGet(`/api/splitwise/groups/${groupId}/balances`)
            ]);

            this.members = members;
            this.expenses = expenses;
            this.balances = balances;

            // Update the UI once with all new data
            this.updateContent();

        } catch (error) {
            this.showMessage(`Failed to refresh group data: ${error.message}`, 'error');
        } finally {
            // Hide loading indicator
        }
    }

    async fetchMembers(groupId) {
        try {
            this.members = await this.apiGet(`/api/splitwise/groups/${groupId}/members`);
            this.updateContent();
        } catch (e) {
            this.showMessage('Failed to fetch members: ' + e.message, 'error');
        }
    }

    async addMember() {
        const name = this.memberNameInput.value.trim();
        if (!name || !this.currentGroupId) return this.showMessage('Please enter a member name', 'warning');

        try {
            await this.apiPost(`/api/splitwise/groups/${this.currentGroupId}/members`, { name });
            this.memberNameInput.value = '';
            this.showMessage('Member added successfully!', 'success');
            await this.refreshGroupData(this.currentGroupId);
        } catch (e) {
            this.showMessage('Failed to add member: ' + e.message, 'error');
        }
    }

    async deleteMember(memberId) {
        if (!confirm('Are you sure you want to delete this member?')) return;

        try {
            await this.apiDelete(`/api/splitwise/members/${memberId}`);
            this.showMessage('Member deleted successfully!', 'success');
            await this.refreshGroupData(this.currentGroupId);
        } catch (e) {
            this.showMessage('Failed to delete member: ' + e.message, 'error');
        }
    }

    async fetchExpenses(groupId) {
        try {
            this.expenses = await this.apiGet(`/api/splitwise/groups/${groupId}/expenses`);
            this.updateContent();
        } catch (e) {
            this.showMessage('Failed to fetch expenses: ' + e.message, 'error');
        }
    }

    async addExpense() {
        const description = this.expenseDescription.value.trim();
        const amount = this.expenseAmount.value;
        const paidById = this.expensePaidBy.value;
        const groupId = this.expenseGroup.value;
        const splitType = this.expenseSplitType.value;

        console.log('Frontend: Adding expense with data:', {
            description, amount, paidById, groupId, splitType
        });

        if (!description || !amount || !paidById || !groupId) {
            return this.showMessage('Please fill in all required fields', 'warning');
        }

        try {
            // Get selected members and their amounts
            const selectedMembers = Array.from(this.expenseSplitMembers.querySelectorAll('input[type="checkbox"]:checked'))
                .map(checkbox => {
                    const memberId = checkbox.value;
                    const amountInput = checkbox.closest('div').querySelector('input[type="number"]');
                    const splitAmount = amountInput ? amountInput.value : '0';
                    return { memberId: parseInt(memberId), amount: splitAmount };
                });

            console.log('Frontend: Selected members:', selectedMembers);

            if (selectedMembers.length === 0) {
                return this.showMessage('Please select at least one member to split with', 'warning');
            }

            const expenseData = {
                description: description,
                amount: amount,
                paidById: parseInt(paidById),
                splitType: splitType
            };

            console.log('Frontend: Sending expense data:', expenseData);
            console.log('Frontend: Sending splits data:', selectedMembers);

            await this.apiPost(`/api/splitwise/groups/${groupId}/expenses`, {
                expense: expenseData,
                splits: selectedMembers
            });

            // Clear form and show message
            this.expenseDescription.value = '';
            this.expenseAmount.value = '';
            this.expensePaidBy.value = '';
            this.expenseSplitType.value = 'EQUAL';
            this.showMessage('Expense added successfully!', 'success');

            // Refresh all group data
            await this.refreshGroupData(groupId);
        } catch (e) {
            console.error('Frontend: Error adding expense:', e);
            this.showMessage('Failed to add expense: ' + e.message, 'error');
        }
    }

    async deleteExpense(expenseId) {
        if (!confirm('Are you sure you want to delete this expense?')) return;

        try {
            await this.apiDelete(`/api/splitwise/expenses/${expenseId}`);
            this.showMessage('Expense deleted successfully!', 'success');
            await this.refreshGroupData(this.currentGroupId);
        } catch (e) {
            this.showMessage('Failed to delete expense: ' + e.message, 'error');
        }
    }

    async fetchBalances(groupId) {
        try {
            this.balances = await this.apiGet(`/api/splitwise/groups/${groupId}/balances`);
            this.updateContent();
        } catch (e) {
            this.showMessage('Failed to fetch balances: ' + e.message, 'error');
        }
    }

    updateSplitMembers() {
        if (this.expenseSplitMembers) {
            this.expenseSplitMembers.innerHTML = this.renderSplitMembersUI();
        }
    }

    updateSplitUI() {
        // Update split UI based on split type
        const splitType = this.expenseSplitType.value;
        const amountInputs = this.expenseSplitMembers.querySelectorAll('input[type="number"]');

        amountInputs.forEach(input => {
            if (splitType === 'EQUAL') {
                input.disabled = true;
                input.value = '';
            } else {
                input.disabled = false;
            }
        });
    }

    showExpenseDetails(expenseId) {
        const expense = this.expenses.find(e => e.id == expenseId);
        if (!expense) return;

        // Show expense details in a modal or expand the expense item
        alert(`Expense Details:\nDescription: ${expense.description}\nAmount: RM ${expense.amount}\nPaid by: ${expense.paidBy ? expense.paidBy.name : 'Unknown'}\nSplit Type: ${expense.splitType}`);
    }

    settleUp() {
        if (!this.currentGroupId) {
            return this.showMessage('Please select a group first.', 'warning');
        }

        if (!confirm('Are you sure you want to settle all balances for this group? This will delete all expenses and cannot be undone.')) {
            return;
        }

        this.settleUpBtn.disabled = true;
        this.settleUpBtn.innerHTML = '🔄 Settling...';

        this.apiPost(`/api/splitwise/groups/${this.currentGroupId}/settle`, {})
            .then(response => {
                this.showMessage(response.message || 'Balances settled successfully!', 'success');
                // Refresh all data to show empty state
                return this.refreshGroupData(this.currentGroupId);
            })
            .catch(error => {
                this.showMessage(`Failed to settle balances: ${error.message}`, 'error');
            })
            .finally(() => {
                this.settleUpBtn.disabled = false;
                this.settleUpBtn.innerHTML = 'Settle All Balances';
            });
    }

    showMessage(message, type = 'success') {
        if (this.statusMessage) {
            this.statusMessage.textContent = message;
            this.statusMessage.className = `text-sm p-2 rounded ${this.getMessageClass(type)}`;
            setTimeout(() => {
                this.statusMessage.textContent = '';
                this.statusMessage.className = '';
            }, 5000);
        }
    }

    getMessageClass(type) {
        switch (type) {
            case 'success': return 'bg-green-100 text-green-800 dark:bg-green-800 dark:text-green-100';
            case 'error': return 'bg-red-100 text-red-800 dark:bg-red-800 dark:text-red-100';
            case 'warning': return 'bg-yellow-100 text-yellow-800 dark:bg-yellow-800 dark:text-yellow-100';
            case 'info': return 'bg-blue-100 text-blue-800 dark:bg-blue-800 dark:text-blue-100';
            default: return 'bg-gray-100 text-gray-800 dark:bg-gray-800 dark:text-gray-100';
        }
    }
}

// Initialize Splitwise when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.splitwise = new Splitwise();
}); 